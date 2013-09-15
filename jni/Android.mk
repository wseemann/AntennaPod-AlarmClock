LOCAL_PATH := $(call my-dir)
#==================================================ICONV
include $(CLEAR_VARS) 

LOCAL_MODULE := libiconv 
LOCAL_CFLAGS := -Wno-multichar \
	-DANDROID \
	-DLIBDIR="\"c\"" \
	-DBUILDING_LIBICONV \
	-DIN_LIBRARY

LOCAL_C_INCLUDES := $(LOCAL_PATH)/libiconv/include \
	$(LOCAL_PATH)/libiconv/lib \
	$(LOCAL_PATH)/libiconv \
	$(LOCAL_PATH)/libiconv/libcharset/lib \
	$(LOCAL_PATH)/libiconv/libcharset/include

LOCAL_SRC_FILES := libiconv/lib/iconv.c \
	libiconv/libcharset/lib/localcharset.c \
	libiconv/lib/relocatable.c

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/libiconv/include
LOCAL_EXPORT_LDLIBS := -lz 

include $(BUILD_STATIC_LIBRARY) 

include $(CLEAR_VARS)

LOCAL_MODULE := boost_regex
LOCAL_SRC_FILES := boost/lib/libboost_regex-gcc-mt.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/boost/include/boost-1_53
LOCAL_EXPORT_LDLIBS := -lz

include $(PREBUILT_STATIC_LIBRARY)

#include $(CLEAR_VARS)

#LOCAL_MODULE := boost_signals
#LOCAL_SRC_FILES := boost/lib/libboost_signals-gcc-mt.a
#LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/boost/include/boost-1_45
#LOCAL_EXPORT_LDLIBS := -lz

#include $(PREBUILT_STATIC_LIBRARY)

#include $(CLEAR_VARS)

#LOCAL_MODULE := boost_filesystem
#LOCAL_SRC_FILES := boost/lib/libboost_filesystem-gcc-mt.a
#LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/boost/include/boost-1_45
#LOCAL_EXPORT_LDLIBS := -lz

#include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := boost_system
LOCAL_SRC_FILES := boost/lib/libboost_system-gcc-mt.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/boost/include/boost-1_53
LOCAL_EXPORT_LDLIBS := -lz

include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE := boost_thread
LOCAL_SRC_FILES := boost/lib/libboost_thread-gcc-mt.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/boost/include/boost-1_53
LOCAL_EXPORT_LDLIBS := -lz

include $(PREBUILT_STATIC_LIBRARY)

#include $(CLEAR_VARS)

#LOCAL_MODULE := boost_date_time
#LOCAL_SRC_FILES := boost/lib/libboost_date_time-gcc-mt.a
#LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/boost/include/boost-1_53
#LOCAL_EXPORT_LDLIBS := -lz

#include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_CPP_EXTENSION := .cpp
LOCAL_MODULE := libtorrent

LOCAL_LDLIBS += -llog \
	-lc \
	-lm \
	-lz \
	-L$(NDK_R8)/sources/cxx-stl/gnu-libstdc++/4.6/libs/armeabi \
	-lgnustl_static

LOCAL_C_INCLUDES += $(LOCAL_PATH)/libtorrent/include
LOCAL_C_INCLUDES += $(LOCAL_PATH)/boost/include/boost-1_53
LOCAL_C_INCLUDES += $(LOCAL_PATH)/libiconv/include

LOCAL_STATIC_LIBRARIES := libiconv \
	boost_regex \
	boost_system \
	boost_thread \

LOCAL_CFLAGS := -DTORRENT_DISABLE_GEO_IP \
	-DTORRENT_DISABLE_ENCRYPTION \
	-g \
	-D_FILE_OFFSET_BITS=64 \
	-DBOOST_FILESYSTEM_NARROW_ONLY \
	-DBOOST_NO_INTRINSIC_WCHAR_T \
	-DTORRENT_HAS_FALLOCATE=0 \
	-DTORRENT_DISABLE_ENCRYPTION \
	-DBOOST_DISABLE_ASSERTS \
	-DTORRENT_USE_ICONV=0 \
	-DTORRENT_USE_MEMALIGN=1 \
	-DTORRENT_NO_DEPRECATE \
	-DBOOST_ASIO_SEPARATE_COMPILATION \
	-D__linux__ \
	-DTORRENT_DISABLE_GEO_IP \
	-fpermissive
	-fPIC \
	-mthumb-interwork \
	-DBOOST_THREAD_LINUX \
	-DBOOST_ASIO_DISABLE_FENCED_BLOCK \
	-DBOOST_HAS_PTHREADS \
	-D__arm__ \
	-D_REENTRANT \
	-D_GLIBCXX__PTHREADS \
	-DANDROID \
	-D__ANDROID__ \
	-nostdlib \
	-fno-short-enums \
	-march=armv5te \
	-mtune=xscale \
	-msoft-float \
	-fomit-frame-pointer \
	-fno-strict-aliasing \
	-frtti \
	-Os \
	-D__cplusplus \
	-std=c++11 \
	-Wall


LOCAL_SRC_FILES := de_danoeh_antennapod_bittorrent_Session.cpp \
	de_danoeh_antennapod_bittorrent_TorrentHandle.cpp \
	de_danoeh_antennapod_bittorrent_TorrentStatus.cpp \
	de_danoeh_antennapod_bittorrent_Alert.cpp \
	libtorrent_util.cpp \
	libtorrent/src/alert.cpp \
	libtorrent/src/allocator.cpp \
	libtorrent/src/asio.cpp \
	libtorrent/src/asio_ssl.cpp \
	libtorrent/src/assert.cpp \
	libtorrent/src/bandwidth_limit.cpp \
	libtorrent/src/bandwidth_manager.cpp \
	libtorrent/src/bandwidth_queue_entry.cpp \
	libtorrent/src/bloom_filter.cpp \
	libtorrent/src/broadcast_socket.cpp \
	libtorrent/src/bt_peer_connection.cpp \
	libtorrent/src/chained_buffer.cpp \
	libtorrent/src/connection_queue.cpp \
	libtorrent/src/ConvertUTF.cpp \
	libtorrent/src/create_torrent.cpp \
	libtorrent/src/disk_buffer_holder.cpp \
	libtorrent/src/disk_buffer_pool.cpp \
	libtorrent/src/disk_io_thread.cpp \
	libtorrent/src/entry.cpp \
	libtorrent/src/enum_net.cpp \
	libtorrent/src/error_code.cpp \
	libtorrent/src/escape_string.cpp \
	libtorrent/src/file.cpp \
	libtorrent/src/file_pool.cpp \
	libtorrent/src/file_storage.cpp \
	libtorrent/src/gzip.cpp \
	libtorrent/src/http_connection.cpp \
	libtorrent/src/http_parser.cpp \
	libtorrent/src/http_seed_connection.cpp \
	libtorrent/src/http_stream.cpp \
	libtorrent/src/http_tracker_connection.cpp \
	libtorrent/src/i2p_stream.cpp \
	libtorrent/src/identify_client.cpp \
	libtorrent/src/instantiate_connection.cpp \
	libtorrent/src/ip_filter.cpp \
	libtorrent/src/kademlia/dht_tracker.cpp \
	libtorrent/src/kademlia/find_data.cpp \
	libtorrent/src/kademlia/node.cpp \
	libtorrent/src/kademlia/node_id.cpp \
	libtorrent/src/kademlia/refresh.cpp \
	libtorrent/src/kademlia/routing_table.cpp \
	libtorrent/src/kademlia/rpc_manager.cpp \
	libtorrent/src/kademlia/traversal_algorithm.cpp \
	libtorrent/src/lazy_bdecode.cpp \
	libtorrent/src/logger.cpp \
	libtorrent/src/lsd.cpp \
	libtorrent/src/lt_trackers.cpp \
	libtorrent/src/magnet_uri.cpp \
	libtorrent/src/metadata_transfer.cpp \
	libtorrent/src/natpmp.cpp \
	libtorrent/src/packet_buffer.cpp \
	libtorrent/src/parse_url.cpp \
	libtorrent/src/pe_crypto.cpp \
	libtorrent/src/peer_connection.cpp \
	libtorrent/src/piece_picker.cpp \
	libtorrent/src/policy.cpp \
	libtorrent/src/puff.cpp \
	libtorrent/src/random.cpp \
	libtorrent/src/rsa.cpp \
	libtorrent/src/rss.cpp \
	libtorrent/src/session.cpp \
	libtorrent/src/session_impl.cpp \
	libtorrent/src/settings.cpp \
	libtorrent/src/sha1.cpp \
	libtorrent/src/smart_ban.cpp \
	libtorrent/src/socket_io.cpp \
	libtorrent/src/socket_type.cpp \
	libtorrent/src/socks5_stream.cpp \
	libtorrent/src/stat.cpp \
	libtorrent/src/storage.cpp \
	libtorrent/src/string_util.cpp \
	libtorrent/src/thread.cpp \
	libtorrent/src/time.cpp \
	libtorrent/src/timestamp_history.cpp \
	libtorrent/src/torrent.cpp \
	libtorrent/src/torrent_handle.cpp \
	libtorrent/src/torrent_info.cpp \
	libtorrent/src/tracker_manager.cpp \
	libtorrent/src/udp_socket.cpp \
	libtorrent/src/udp_tracker_connection.cpp \
	libtorrent/src/upnp.cpp \
	libtorrent/src/ut_metadata.cpp \
	libtorrent/src/ut_pex.cpp \
	libtorrent/src/utf8.cpp \
	libtorrent/src/utp_socket_manager.cpp \
	libtorrent/src/utp_stream.cpp \
	libtorrent/src/web_connection_base.cpp \
	libtorrent/src/web_peer_connection.cpp \

#LOCAL_SRC_FILES := libtorrent.cpp \
	libtorrent/src/alert.cpp \
	libtorrent/src/allocator.cpp \
	libtorrent/src/assert.cpp \
	libtorrent/src/broadcast_socket.cpp \
	libtorrent/src/bt_peer_connection.cpp \
	libtorrent/src/connection_queue.cpp \
	libtorrent/src/ConvertUTF.cpp \
	libtorrent/src/create_torrent.cpp \
	libtorrent/src/disk_buffer_holder.cpp \
	libtorrent/src/disk_io_thread.cpp \
	libtorrent/src/entry.cpp \
	libtorrent/src/enum_net.cpp \
	libtorrent/src/error_code.cpp \
	libtorrent/src/escape_string.cpp \
	libtorrent/src/file.cpp \
	libtorrent/src/file_pool.cpp \
	libtorrent/src/file_storage.cpp \
	libtorrent/src/gzip.cpp \
	libtorrent/src/http_connection.cpp \
	libtorrent/src/http_parser.cpp \
	libtorrent/src/http_seed_connection.cpp \
	libtorrent/src/http_stream.cpp \
	libtorrent/src/http_tracker_connection.cpp \
	libtorrent/src/identify_client.cpp \
	libtorrent/src/instantiate_connection.cpp \
	libtorrent/src/ip_filter.cpp \
	libtorrent/src/kademlia/closest_nodes.cpp \
	libtorrent/src/kademlia/dht_tracker.cpp \
	libtorrent/src/kademlia/find_data.cpp \
	libtorrent/src/kademlia/node.cpp \
	libtorrent/src/kademlia/node_id.cpp \
	libtorrent/src/kademlia/refresh.cpp \
	libtorrent/src/kademlia/routing_table.cpp \
	libtorrent/src/kademlia/rpc_manager.cpp \
	libtorrent/src/kademlia/traversal_algorithm.cpp \
	libtorrent/src/lazy_bdecode.cpp \
	libtorrent/src/logger.cpp \
	libtorrent/src/lsd.cpp \
	libtorrent/src/lt_trackers.cpp \
	libtorrent/src/magnet_uri.cpp \
	libtorrent/src/metadata_transfer.cpp \
	libtorrent/src/natpmp.cpp \
	libtorrent/src/parse_url.cpp \
	libtorrent/src/pe_crypto.cpp \
	libtorrent/src/peer_connection.cpp \
	libtorrent/src/piece_picker.cpp \
	libtorrent/src/policy.cpp \
	libtorrent/src/session.cpp \
	libtorrent/src/session_impl.cpp \
	libtorrent/src/sha1.cpp \
	libtorrent/src/smart_ban.cpp \
	libtorrent/src/socks5_stream.cpp \
	libtorrent/src/stat.cpp \
	libtorrent/src/storage.cpp \
	libtorrent/src/torrent.cpp \
	libtorrent/src/torrent_handle.cpp \
	libtorrent/src/torrent_info.cpp \
	libtorrent/src/tracker_manager.cpp \
	libtorrent/src/udp_socket.cpp \
	libtorrent/src/udp_tracker_connection.cpp \
	libtorrent/src/upnp.cpp \
	libtorrent/src/ut_metadata.cpp \
	libtorrent/src/ut_pex.cpp \
	libtorrent/src/web_peer_connection.cpp \

include $(BUILD_SHARED_LIBRARY)
