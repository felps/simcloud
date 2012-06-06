#ifndef CHOR_STRUCTS
#define CHOR_STRUCTS

enum {INVOKE,EXEC,WAIT,RETURN,INVOKE_A};

typedef struct method_instruction {
    int type;//invoke or exec
    char role[100];
    char method_name[100];
    double input_size;
}method_instruction;

typedef struct method_data {
    char method[100];
    double exec_time;
    double output_size;
}method_data;

typedef struct chor_method_data{
     char method[100];
    double exec_time;
    double output_size;
    method_instruction instructions[30];
    int instructions_len;
}chor_method_data;

typedef struct request{
    char role[100];
    char method[100];
    double input_size;
}request;

typedef struct task_data{
    double id;
    double starttime;
    char sender[100];
}task_data;
#endif


int service_invoke_cordel(char role[],char method_name[],double size,task_data *data);
int service_cordel_controller(int argc,char *argv[]);
int service_cordel(int argc,char *argv[]);
void run_service_cordel(int list_methods_len,chor_method_data list_methods[],char mailbox[]);
int wait_task(char sender_name[],int waiting_count);
int service_chor_controller(int argc,char *argv[]);
int service_invoke(char role[],char method_name[],double size,char sender_name[]);
int find_method(int list_methods_len,chor_method_data list_methods[],char task_name[]);
void run_chor_task(char task_name[],chor_method_data method_data);
void run_service(int list_method_len,chor_method_data data[],char mailbox[]);
void make_instruction(method_instruction *instruction,int type,char role[], char method_name[], double input_size);
int service_chor(int argc,char *argv[]);

void make_chor_method(chor_method_data *method_data,char method[], char exec_time[], char output_size[]);

int broker_controller(int argc,char *argv[]);
int service_controller(int argc,char *argv[]);
int service(int argc, char *argv[]);
MSG_error_t run(const char *platform_file,const char *application_file);
int broker(int argc, char *argv[]);
void make_method(method_data *data,char method[], char exec_time[], char output_size[]);
void make_request(request *r,char role[], char method[],char input_size[]);
void finalize_tasks();
void run_task(char task_name[],int list_methods_len,method_data list_methods[],m_task_t received_task);
void return_task(char task_name[],m_task_t received_task,double output_size);
task_data receive_task(char mailbox[]);
void create_task_data(task_data *data,double task_id, char sender[]);
void write_log(double starttime,double endtime,char text[]);
