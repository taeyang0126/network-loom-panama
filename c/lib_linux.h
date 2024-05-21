#ifndef LIB_LINUX_H
#define LIB_LINUX_H

#include "share.h"
#include <stdint.h>
#include <sys/epoll.h>
#include <netinet/in.h>
#include <sys/socket.h>

EXPORT_SYMBOL int l_connect_block_code();

EXPORT_SYMBOL int l_send_block_code();

EXPORT_SYMBOL int l_interrupt_code();

EXPORT_SYMBOL int l_ipv4_address_len();

EXPORT_SYMBOL int l_ipv6_address_len();

EXPORT_SYMBOL int l_ipv4_address_size();

EXPORT_SYMBOL int l_ipv6_address_size();

EXPORT_SYMBOL int l_ipv4_address_align();

EXPORT_SYMBOL int l_ipv6_address_align();

EXPORT_SYMBOL int l_epoll_create();

EXPORT_SYMBOL int l_epoll_ctl(int epfd, int op, int socket, struct epoll_event *ev);

EXPORT_SYMBOL int l_epoll_wait(int epfd, struct epoll_event *events, int maxEvents, int timeout);

EXPORT_SYMBOL int l_get_ipv4_address(struct sockaddr_in *sockAddr, char *addrStr, socklen_t len);

EXPORT_SYMBOL int l_get_ipv6_address(struct sockaddr_in6 *sockAddr, char *addrStr, socklen_t len);

EXPORT_SYMBOL uint16_t l_ipv4_port(struct sockaddr_in *sockAddr);

EXPORT_SYMBOL uint16_t l_ipv6_port(struct sockaddr_in6 *sockAddr);

EXPORT_SYMBOL int l_ipv4_socket_create();

EXPORT_SYMBOL int l_ipv6_socket_create();

EXPORT_SYMBOL int l_set_ipv4_sock_addr(struct sockaddr_in *sockAddr, char *address, uint16_t port);

EXPORT_SYMBOL int l_set_ipv6_sock_addr(struct sockaddr_in6 *sockAddr, char *address, uint16_t port);

EXPORT_SYMBOL int l_set_reuse_addr(int socket, int value);

EXPORT_SYMBOL int l_set_keep_alive(int socket, int value);

EXPORT_SYMBOL int l_set_tcp_no_delay(int socket, int value);

EXPORT_SYMBOL int l_set_ipv6_only(int socket, int value);

EXPORT_SYMBOL int l_get_err_opt(int socket, int *ptr);

EXPORT_SYMBOL int l_set_nonblocking(int socket);

EXPORT_SYMBOL int l_bind(int socket, void *sockAddr, socklen_t size);

EXPORT_SYMBOL int l_listen(int socket, int backlog);

EXPORT_SYMBOL int l_connect(int socket, void *sockAddr, socklen_t size);

EXPORT_SYMBOL int l_accept(int socket, void *clientAddr, socklen_t clientAddrSize);

EXPORT_SYMBOL ssize_t l_recv(int socket, void *buf, size_t len);

EXPORT_SYMBOL ssize_t l_send(int socket, void *buf, size_t len);

EXPORT_SYMBOL int l_shutdown_write(int fd);

EXPORT_SYMBOL int l_close(int fd);

#endif