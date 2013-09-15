#include "de_danoeh_antennapod_bittorrent_TorrentStatus.h"

#include "libtorrent/torrent_handle.hpp"

using namespace libtorrent;

static torrent_status& get_torrent_status_handle(JNIEnv *env, jobject obj)
{
    jclass c = env->GetObjectClass(obj);
    jfieldID fieldID =  env->GetFieldID(c, "nativeObj", "J");
    jlong addr = env->GetLongField(obj, fieldID);
    return *(reinterpret_cast<torrent_status *>(addr));
}

JNIEXPORT jint JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentStatus_n_1state
  (JNIEnv *env, jobject obj)
{
    torrent_status& ts = get_torrent_status_handle(env, obj);
    return (jint) ts.state;
}

JNIEXPORT jboolean JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentStatus_n_1paused
  (JNIEnv *env, jobject obj)
{
    torrent_status& ts = get_torrent_status_handle(env, obj);
    return ts.paused;
}

JNIEXPORT jboolean JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentStatus_n_1auto_1managed
  (JNIEnv *env, jobject obj)
{
    torrent_status& ts = get_torrent_status_handle(env, obj);
    return ts.auto_managed;
}
JNIEXPORT jboolean JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentStatus_n_1sequential_1download
  (JNIEnv *env, jobject obj)
{
    torrent_status& ts = get_torrent_status_handle(env, obj);
    return ts.sequential_download;
}


JNIEXPORT jboolean JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentStatus_n_1seeding
  (JNIEnv *env, jobject obj)
{
    torrent_status& ts = get_torrent_status_handle(env, obj);
    return ts.seeding;
}

JNIEXPORT jboolean JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentStatus_n_1finished
  (JNIEnv *env, jobject obj)
{
    torrent_status& ts = get_torrent_status_handle(env, obj);
    return ts.finished;
}

JNIEXPORT jfloat JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentStatus_n_1progress
  (JNIEnv *env, jobject obj)
{
    torrent_status& ts = get_torrent_status_handle(env, obj);
    return ts.progress;
}

JNIEXPORT jlong JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentStatus_n_1total_1download
  (JNIEnv *env, jobject obj)
{
    torrent_status& ts = get_torrent_status_handle(env, obj);
    return ts.total_download;
}

JNIEXPORT jlong JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentStatus_n_1total_1upload
  (JNIEnv *env, jobject obj)
{
    torrent_status& ts = get_torrent_status_handle(env, obj);
    return ts.total_upload;
}

JNIEXPORT jint JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentStatus_n_1download_1rate
  (JNIEnv *env, jobject obj)
{
    torrent_status& ts = get_torrent_status_handle(env, obj);
    return ts.download_rate;
}

JNIEXPORT jint JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentStatus_n_1upload_1rate
  (JNIEnv *env, jobject obj)
{
    torrent_status& ts = get_torrent_status_handle(env, obj);
    return ts.upload_rate;
}

JNIEXPORT jlong JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentStatus_n_1total_1done
  (JNIEnv *env, jobject obj)
{
    torrent_status& ts = get_torrent_status_handle(env, obj);
    return ts.total_done;
}

JNIEXPORT jlong JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentStatus_n_1total_1wanted_1done
  (JNIEnv *env, jobject obj)
{
    torrent_status& ts = get_torrent_status_handle(env, obj);
    return ts.total_wanted_done;
}

JNIEXPORT jlong JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentStatus_n_1total_1wanted
  (JNIEnv *env, jobject obj)
{
    torrent_status& ts = get_torrent_status_handle(env, obj);
    return ts.total_wanted;
}

JNIEXPORT void JNICALL Java_de_danoeh_antennapod_bittorrent_TorrentStatus_n_1delete
  (JNIEnv *env, jobject obj)
{
    torrent_status& ts = get_torrent_status_handle(env, obj);
    free(&ts);
}
