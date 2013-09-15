
#include <jni.h>
#include "libtorrent/torrent_handle.hpp"
#include "libtorrent/alert.hpp"

void throwLibtorrentException(JNIEnv *, libtorrent::libtorrent_exception);
jobject new_alert(JNIEnv *, libtorrent::alert *);
