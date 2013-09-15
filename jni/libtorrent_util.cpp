#ifndef LIBTORRENT_UTIL_HPP_INCLUDED
#define LIBTORRENT_UTIL_HPP_INCLUDED

#include "libtorrent_util.hpp"

using namespace libtorrent;

void throwLibtorrentException(JNIEnv *env, libtorrent_exception ex) {
    jclass c = env->FindClass("de/danoeh/antennapod/bittorrent/LibtorrentException");
    env->ThrowNew(c, ex.what());
}

#endif
