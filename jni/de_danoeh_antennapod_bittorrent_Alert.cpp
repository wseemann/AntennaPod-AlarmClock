#include "de_danoeh_antennapod_bittorrent_Alert.h"
#include "libtorrent_util.hpp"

#include <string>
#include "libtorrent/alert.hpp"
#include "libtorrent/alert_types.hpp"

using namespace libtorrent;

jobject new_alert(JNIEnv *env, int type, std::string& msg) { 
    jstring j_msg = (&msg) ? env->NewStringUTF(msg.data()) : 0;
    jclass c = env->FindClass("de/danoeh/antennapod/bittorrent/Alert");
    jmethodID method_id = env->GetMethodID(c, "<init>", "(ILjava/lang/String;)V");
    return env->NewObject(c , method_id, type, j_msg);
}

jobject new_torrent_alert(JNIEnv *env, int type, std::string& msg, torrent_handle *th) {
    jstring j_msg = (&msg) ? env->NewStringUTF(msg.data()) : 0;
    jclass c = env->FindClass("de/danoeh/antennapod/bittorrent/TorrentAlert");
    jmethodID method_id = env->GetMethodID(c, "<init>", "(ILjava/lang/String;J)V");
    return env->NewObject(c , method_id, type, j_msg, (jlong) th);
}

jobject new_alert(JNIEnv *env, alert *al)
{
    torrent_handle *h;
    std::string msg = al->message();

    switch(al->type())
    {
        case torrent_added_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_TORRENT_ADDED, msg, h);
        case add_torrent_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_ADD_TORRENT, msg, h);
        case torrent_removed_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_TORRENT_REMOVED, msg, h);
        case read_piece_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_READ_PIECE, msg, h);
        case external_ip_alert::alert_type:
            return new_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_EXTERNAL_IP, msg);
        case listen_failed_alert::alert_type:
            return new_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_LISTEN_FAILED, msg);
        case listen_succeeded_alert::alert_type:
            return new_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_LISTEN_SUCCEEDED, msg);
        case portmap_error_alert::alert_type:
            return new_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_PORTMAP_ERROR, msg);
        case portmap_alert::alert_type:
            return new_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_PORTMAP, msg);
        case portmap_log_alert::alert_type:
            return new_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_PORTMAP_LOG, msg);
        case file_error_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_FILE_ERROR, msg, h);
        case torrent_error_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_TORRENT_ERROR, msg, h);
        case file_renamed_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_FILE_RENAMED, msg, h);
        case file_rename_failed_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_FILE_RENAME_FAILED, msg, h);
        case tracker_announce_alert::alert_type:
            return new_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_TRACKER_ANNOUNCE, msg);
        case tracker_error_alert::alert_type:
            return new_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_TRACKER_ERROR, msg);
        case tracker_reply_alert::alert_type:
            return new_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_TRACKER_REPLY, msg);
        case tracker_warning_alert::alert_type:
            return new_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_TRACKER_WARNING, msg);
        case scrape_reply_alert::alert_type:
            return new_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_SCRAPE_REPLY, msg);
        case scrape_failed_alert::alert_type:
            return new_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_SCRAPE_FAILED, msg);
        case url_seed_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_URL_SEED, msg, h);
        case hash_failed_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_HASH_FAILED, msg, h);
        case peer_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_PEER, msg, h);
        case peer_connect_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_PEER_CONNECT, msg, h);
        case peer_ban_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_PEER_BAN, msg, h);
        case peer_snubbed_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_PEER_SNUBBED, msg, h);
        case peer_unsnubbed_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_PEER_UNSNUBBED, msg, h);
        case peer_error_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_PEER_ERROR, msg, h);
        case peer_disconnected_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_PEER_DISCONNECTED, msg, h);
        case invalid_request_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_INVALID_REQUEST, msg, h);
        case request_dropped_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_REQUEST_DROPPED, msg, h);
        case block_timeout_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_BLOCK_TIMEOUT, msg, h);
        case block_finished_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_BLOCK_FINISHED, msg, h);
        case lsd_peer_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_LSD_PEER, msg, h);
        case file_completed_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_FILE_COMPLETED, msg, h);
        case block_downloading_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_BLOCK_DOWNLOADING, msg, h);
        case unwanted_block_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_UNWANTED_BLOCK, msg, h);
        case torrent_delete_failed_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_TORRENT_DELETE_FAILED, msg, h);
        case torrent_deleted_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_TORRENT_DELETED, msg, h);
        case torrent_finished_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_TORRENT_FINISHED, msg, h);
        case performance_alert::alert_type:
            h = &(((torrent_alert *)al)->handle);
            return new_torrent_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_PERFORMANCE, msg, h);
        default:
            return new_alert(env, de_danoeh_antennapod_bittorrent_Alert_TYPE_UNKOWN, msg);
    }
}
