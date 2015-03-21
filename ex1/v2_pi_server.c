#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <signal.h>

#define htonll(value) ((((uint64_t)htonl(value & 0xFFFFFFFF)) << 32LL) | htonl(value >> 32))

#define PORT 4444
int server_socket;
int client_socket;

void intHandler(int not_used);

int open_server_socket(void);

void closeSocket(void);

uint8_t read_byte(int client_socket);

uint16_t read_short(int client_socket);

uint32_t read_int(int client_socket);

uint64_t read_long(int client_socket);

void listen_for_clients(void);

uint8_t compute_nth_pi_digit(uint64_t requested_digit);

void close_sockets() {
    close(client_socket);
    close(server_socket);
}

int main(int argc, char *argv[]) {

    //listen to ctrl+c to close socket on that
    signal(SIGINT, intHandler);

    //start server
    server_socket = open_server_socket();

    //listen for requests
    while (1) {
        listen_for_clients();
    }

    return 0;
}

void intHandler(int not_used) {
    printf("\n\nBye bye! Closing sockets");
    close_sockets();
    exit(EXIT_SUCCESS);
}

uint8_t read_byte(int client_socket) {
    uint8_t num;
    recv(client_socket, &num, 1, 0);
    printf("Received: %d\n", num);
    return num;
}

uint16_t read_short(int client_socket) {
    uint16_t num;
    recv(client_socket, &num, 2, 0);
    num = ntohs(num);
    printf("Received: %d\n", num);
    return num;
}

uint32_t read_int(int client_socket) {
    uint32_t num;
    recv(client_socket, &num, 4, 0);
    num = ntohl(num);
    printf("Received: %d\n", num);
    return num;
}

uint64_t read_long(int client_socket) {
    uint64_t num;
    recv(client_socket, &num, 8, 0);
    num = htonll(num);
    printf("Received: %ld\n", num);
    return num;
}

u_int8_t pi[] = {3, 1, 4, 1, 5, 9, 2};

uint8_t compute_nth_pi_digit(uint64_t requested_digit) {
    if (requested_digit < sizeof(pi)) {
        return pi[(uint32_t)requested_digit];
    }
    return 3;
}

void listen_for_clients() {
    struct sockaddr_in client_address;
    socklen_t client_address_len = sizeof(client_address);

    client_socket = accept(server_socket, (struct sockaddr *) &client_address, &client_address_len);
    printf("Client connected on port %d\n", client_address.sin_port);


    uint64_t requested_digit;
    while (1) {
        char num_size = read_byte(client_socket);
        switch (num_size) {
            case 1:
                requested_digit = read_byte(client_socket);
                printf("Reveived byte type (1B) with value %d\n", (u_int8_t)requested_digit);
                break;
            case 2:
                requested_digit = read_short(client_socket);
                printf("Reveived short type (2B) with value %d\n", (uint16_t) requested_digit);
                break;
            case 4:
                requested_digit = read_int(client_socket);
                printf("Reveived int type (4B) with value %d\n", (uint32_t) requested_digit);
                break;
            case 8:
                requested_digit = read_long(client_socket);
                printf("Reveived long type (8B) with value %ld\n", requested_digit);
                break;
            default:
                printf("Unsupported data type! %d\n", num_size);
        }
        uint8_t num = compute_nth_pi_digit(requested_digit);
        printf("Sending response. Listening for another request...\n");
        send(client_socket, &num, 1, 0);
    }
}

int open_server_socket(void) {
    int server_socket;
    struct sockaddr_in server_address;

    if ((server_socket = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        printf("Failure opening server socket\n");
        exit(EXIT_FAILURE);
    }

    memset(&server_address, 0, sizeof(server_address));
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = htonl(INADDR_ANY);
    server_address.sin_port = htons(PORT);

    if (bind(server_socket, (struct sockaddr *) &server_address, sizeof(server_address)) < 0) {
        perror("Failure binding socket\n");
        close(server_socket);
        exit(EXIT_FAILURE);
    }

    int queue_size = 5;
    if (listen(server_socket, queue_size) == -1) {
        perror("Failure listening on socket");
        close(server_socket);
        exit(EXIT_FAILURE);
    }
    printf("Server listening on port # %d\n", PORT);

    return server_socket;
}
