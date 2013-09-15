#include "de_danoeh_antennapod_bittorrent_TorrentHandle.h"
#include "libtorrent_util.hpp"

#include "libtorrent/torrent_handle.hpp"

#include <string>
#include <cstdlib>

using namespace libtorrent;

static torrent_status *store_torrent_status(torrent_status *handle) {
    torrent_status *res = (torrent_status *) malloc(sizeof(torrent_status));
    if (!res) {
        throw std::bad_alloc();
    }
    memcpy(res, handle, sizeof(torrent_status));
    return res;
}

static torrent_handle& get_torrent_handle_handle(JNIEnv *env, jobject obj)
{
    jclass c = env->GetObjectClass(obj);
    jfieldID fieldID =  env->GetFieldID(c, "nativeObj", "J");
    jlong addr = env->GetLongField(obj, fieldID);
    return *(reinterpret_cast<torrent_handle *>(addr));
}

    JNIEXPORT jstring JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentHandle_n_1get_1name
(JNIEnv *env, jobject obj)
{
    torrent_handle& th = get_torrent_handle_handle(env, obj);
    try {
        std::string name = th.name();
        return env->NewStringUTF(name.data());
    } catch (libtorrent_exception e) {
        throwLibtorrentException(env, e); 
    }
}


    JNIEXPORT void JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentHandle_n_1set_1sequential
(JNIEnv *env, jobject obj, jboolean sd)
{
    torrent_handle& th = get_torrent_handle_handle(env, obj);
    try {
        th.set_sequential_download(sd);
    } catch (libtorrent_exception e) {
        throwLibtorrentException(env, e); 
    }
}

    JNIEXPORT void JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentHandle_n_1set_1upload_1mode
(JNIEnv *env, jobject obj, jboolean m)
{
    torrent_handle& th = get_torrent_handle_handle(env, obj);
    try {
        th.set_upload_mode(m);
    } catch (libtorrent_exception e) {
        throwLibtorrentException(env, e); 
    }
}

    JNIEXPORT void JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentHandle_n_1set_1share_1mode
(JNIEnv *env, jobject obj, jboolean m) 
{
    torrent_handle& th = get_torrent_handle_handle(env, obj);
    try {
        th.set_share_mode(m);
    } catch (libtorrent_exception e) {
        throwLibtorrentException(env, e); 
    }
}

    JNIEXPORT void JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentHandle_n_1set_1priority
(JNIEnv *env, jobject obj, jint priority)
{
    torrent_handle& th = get_torrent_handle_handle(env, obj);
    try {
        return th.set_priority(priority);
    } catch (libtorrent_exception e) {
        throwLibtorrentException(env, e); 
    }
}

    JNIEXPORT jboolean JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentHandle_n_1is_1valid
(JNIEnv *env, jobject obj)
{
    torrent_handle& th = get_torrent_handle_handle(env, obj);
    try {
        return th.is_valid();
    } catch (libtorrent_exception e) {
        throwLibtorrentException(env, e); 
    }
}

    JNIEXPORT jlong JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentHandle_n_1get_1status
(JNIEnv *env, jobject obj)
{
    torrent_handle& th = get_torrent_handle_handle(env, obj);
    try {
        torrent_status s = th.status(torrent_handle::query_accurate_download_counters);
        return (jlong) store_torrent_status(&s);
    } catch (libtorrent_exception e) {
        throwLibtorrentException(env, e); 
    }
}

    JNIEXPORT void JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentHandle_n_1delete
(JNIEnv *env, jobject obj)
{
    torrent_handle& th = get_torrent_handle_handle(env, obj);
    delete &th;
}

    JNIEXPORT jint JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentHandle_n_1num_1files
(JNIEnv *env, jobject obj)
{
    torrent_handle& th = get_torrent_handle_handle(env, obj);
    try {
        return th.get_torrent_info().num_files();
    } catch (libtorrent_exception& e) {
        throwLibtorrentException(env, e);
    }
}

    JNIEXPORT jstring JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentHandle_n_1file_1at
(JNIEnv *env, jobject obj, jint index)
{
    torrent_handle& th = get_torrent_handle_handle(env, obj);
    try {
        file_entry f = th.get_torrent_info().file_at(index);
        return env->NewStringUTF(f.path.data());
    } catch (libtorrent_exception& e) {
        throwLibtorrentException(env, e);
    }
}

    JNIEXPORT void JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentHandle_n_1rename_1file
(JNIEnv *env, jobject obj, jint index, jstring name)
{
    torrent_handle& th = get_torrent_handle_handle(env, obj);
    const char *name_str = env->GetStringUTFChars(name, 0); 
    std::string name_std_str(name_str);
    env->ReleaseStringUTFChars(name, name_str);
    try {
        th.rename_file(index, name_std_str);    
    } catch (libtorrent_exception& e) {
        throwLibtorrentException(env, e);
    }
}
