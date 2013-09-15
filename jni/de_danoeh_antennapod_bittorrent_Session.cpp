#include "de_danoeh_antennapod_bittorrent_Session.h"
#include "libtorrent_util.hpp"
#include "de_danoeh_antennapod_bittorrent_Alert.h"

#include <utility>
#include <cstring>
#include <exception>
#include <new>
#include <string>
#include <memory>

#include "libtorrent/session.hpp"
#include "libtorrent/torrent_handle.hpp"
#include "libtorrent/error_code.hpp"
#include "libtorrent/alert.hpp"
#include "boost/intrusive_ptr.hpp"

using namespace libtorrent;

static session& get_session_handle(JNIEnv *env, jobject obj)
{
    jclass c = env->GetObjectClass(obj);
    jfieldID fieldID =  env->GetFieldID(c, "nativeObj", "J");
    jlong addr = env->GetLongField(obj, fieldID);
    return *(reinterpret_cast<session *>(addr));
}

static torrent_handle *store_torrent_handle(torrent_handle *handle)
{
    torrent_handle *res = new torrent_handle();
    *res = *handle;
    return res;
}

    JNIEXPORT jlong JNICALL Java_de_danoeh_antennapod_bittorrent_Session_n_1session
(JNIEnv *env, jobject obj)
{
    session *s = new session();
    s->set_alert_mask(alert::all_categories);
    return (jlong) s;
}


    JNIEXPORT void JNICALL Java_de_danoeh_antennapod_bittorrent_Session_n_1listen_1on
(JNIEnv *env, jobject obj, jint start, jint end)
{
    session& s = get_session_handle(env, obj);
    error_code ec;
    s.listen_on(std::make_pair(start, end), ec);
}

    JNIEXPORT jlong JNICALL Java_de_danoeh_antennapod_bittorrent_Session_n_1add_1torrent_1url
(JNIEnv *env, jobject obj, jstring save_path, jstring url, jint flags)
{
    session& s = get_session_handle(env, obj); 
    add_torrent_params params;
    const char *save_path_str = env->GetStringUTFChars(save_path, 0);
    const char *url_str = env->GetStringUTFChars(url, 0);

    params.save_path = std::string{save_path_str};
    params.url = std::string{url_str};
    params.flags = flags;

    env->ReleaseStringUTFChars(save_path, save_path_str);
    env->ReleaseStringUTFChars(url, url_str);
    try {
        torrent_handle handle = s.add_torrent(params);
        return (jlong) store_torrent_handle(&handle);
    } catch (libtorrent_exception& e) {
        throwLibtorrentException(env, e);
    }
}

    JNIEXPORT jlong JNICALL Java_de_danoeh_antennapod_bittorrent_Session_n_1add_1torrent_1file
(JNIEnv *env, jobject obj, jstring save_path, jstring info_path, jint flags)
{
    session& s = get_session_handle(env, obj); 
    add_torrent_params params;
    const char *save_path_str = env->GetStringUTFChars(save_path, 0);
    const char *info_path_str = env->GetStringUTFChars(info_path, 0);

    params.save_path = std::string{save_path_str};
    params.ti = boost::intrusive_ptr<torrent_info>(&torrent_info(std::string(info_path_str)));
    params.flags = flags;

    env->ReleaseStringUTFChars(save_path, save_path_str);
    env->ReleaseStringUTFChars(info_path, info_path_str);

    try {
        torrent_handle handle = s.add_torrent(params);
        return (jlong) store_torrent_handle(&handle);
    } catch (libtorrent_exception& e) {
        throwLibtorrentException(env, e);
    }
}

    JNIEXPORT void JNICALL Java_de_danoeh_antennapod_bittorrent_Session_n_1remove_1torrent
(JNIEnv *env, jobject obj, jlong handle_addr)
{
    session& s = get_session_handle(env, obj); 
    torrent_handle& th = *(reinterpret_cast<torrent_handle *>(handle_addr));
    s.remove_torrent(th);
}

    JNIEXPORT void JNICALL Java_de_danoeh_antennapod_bittorrent_Session_n_1shutdown
(JNIEnv *env, jobject obj)
{
    session& s = get_session_handle(env, obj);
    s.abort();
}

    JNIEXPORT void JNICALL Java_de_danoeh_antennapod_bittorrent_Session_n_1delete
(JNIEnv *env, jobject obj)
{
    session& s = get_session_handle(env, obj);
    delete &s;
}

JNIEXPORT jobject JNICALL Java_de_danoeh_antennapod_bittorrent_Session_n_1pop_1alert
  (JNIEnv *env, jobject obj)
{
    session& ses = get_session_handle(env, obj);
    std::auto_ptr<alert> a = ses.pop_alert();
    if (a.get()) {
        return new_alert(env, a.get());
    } else {
        return NULL;
    }
}


JNIEXPORT jobject JNICALL Java_de_danoeh_antennapod_bittorrent_Session_n_1wait_1for_1alert
  (JNIEnv *env, jobject obj, jlong max_wait)
{
    session& ses = get_session_handle(env, obj);
    const alert *a = ses.wait_for_alert((time_duration) max_wait);
    if (a) {
        return new_alert(env, a);
    } else {
        return NULL;
    }
}
