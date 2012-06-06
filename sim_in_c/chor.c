#include <stdio.h>
#include <limits.h>
#include "msg/msg.h"            
#include "xbt/sysdep.h"         /* calloc, printf */

/* Create a log channel to have nice outputs. */
#include "xbt/log.h"
#include "xbt/asserts.h"
#include "chor.h"
XBT_LOG_NEW_DEFAULT_CATEGORY(msg_info,
                             "log messages");

int MAX_TIME_SIMULATION=INT_MAX;
double static task_id = 0;

int service_controller(int argc,char *argv[]){
    int count = argc-1;
    char **arg=(char **)calloc(count,sizeof(char*));
    int process_number = atoi(argv[1]);
    m_process_t process_list[process_number];
    arg[0] = strdup("service");
    int i;
    for(i=2;i<argc;i++){
	arg[i-1] = strdup(argv[i]);
    }
    
    XBT_INFO("initializing service: %s",arg[1]);
    for(i=2;i<count;i=i+3)
	XBT_INFO("method:%s,time to exec:  %s,output size: %s",arg[i],arg[i+1],arg[i+2]);

    //create a number of process
    for(i=0;i<process_number;i++){
	process_list[i] = MSG_process_create_with_arguments(arg[0],service,NULL,MSG_host_self(),count,arg);
    }
    MSG_process_sleep(MAX_TIME_SIMULATION);    
    //kill all process in process_list
    for(i=0;i<process_number;i++){
	MSG_process_kill(process_list[i]);
    }
}

int service_cordel_controller(int argc,char *argv[]){
    int count = argc-1;
    char **arg=(char **)calloc(count,sizeof(char*));
    int process_number = atoi(argv[1]);
    m_process_t process_list[process_number];
    arg[0] = strdup("service_cordel");
    int i;
    for(i=2;i<argc;i++){
	arg[i-1] = strdup(argv[i]);
    }
    
    XBT_INFO("initializing service: %s",arg[1]);

    //create a number of process
    for(i=0;i<process_number;i++){
	process_list[i] = MSG_process_create_with_arguments(arg[0],service_cordel,NULL,MSG_host_self(),count,arg);
    }
    MSG_process_sleep(MAX_TIME_SIMULATION);    
    //kill all process in process_list
    for(i=0;i<process_number;i++){
	MSG_process_kill(process_list[i]);
    }
}

int service_cordel(int argc,char *argv[]){
    chor_method_data list_methods[12];
    int list_methods_len = 0;
    char mailbox[50];
    strcpy(mailbox,argv[1]);
    XBT_INFO("created service %s",mailbox);
    int count_arg = 2;
    while(count_arg < argc){
	//add method
	if(strcmp(argv[count_arg],"method") == 0){
	    count_arg++;
	    make_chor_method(&list_methods[list_methods_len],argv[count_arg],argv[count_arg+1],argv[count_arg+2]);

	    XBT_INFO("method: %s",argv[count_arg]);
	    count_arg = count_arg+3;
	    //add instructions
	    int number_of_instruction = 0;
	    while(strcmp(argv[count_arg],"method") != 0){
		if(strcmp(argv[count_arg],"invoke") == 0){
		    make_instruction(&list_methods[list_methods_len].instructions[number_of_instruction],INVOKE,argv[count_arg+1],argv[count_arg+2],atof(argv[count_arg+3]));
		    count_arg = count_arg+4;
		}else if(strcmp(argv[count_arg],"invoke_a") == 0){
		    make_instruction(&list_methods[list_methods_len].instructions[number_of_instruction],INVOKE_A,argv[count_arg+1],argv[count_arg+2],atof(argv[count_arg+3]));
		    count_arg = count_arg+4;
		}else if(strcmp(argv[count_arg],"exec") == 0){
			list_methods[list_methods_len].instructions[number_of_instruction].type = EXEC;
			count_arg++;
		}else if(strcmp(argv[count_arg],"wait") == 0){
			list_methods[list_methods_len].instructions[number_of_instruction].type = WAIT;
			count_arg++;
		}else if(strcmp(argv[count_arg],"return") == 0){
			list_methods[list_methods_len].instructions[number_of_instruction].type = RETURN;
			count_arg++;
		    }else{
			XBT_WARN("parser arguments error");
			return 1;}
		number_of_instruction++;
		if(count_arg >= argc){
		    break;
		}
	    }
	    list_methods[list_methods_len].instructions_len = number_of_instruction;
	    list_methods_len++;
	}
    }
    
    run_service_cordel(list_methods_len,list_methods,mailbox);
}

void run_service_cordel(int list_methods_len,chor_method_data list_methods[],char mailbox[]){
    m_task_t received_task = NULL;
    MSG_error_t res = MSG_OK;
    msg_comm_t msg_recv = NULL;
    static int id_sender = 0;
    char sender_name[50];
    sprintf(sender_name,"%s_%d",mailbox,id_sender++);
    int waiting_count = 0;
    while(1){
	msg_recv = MSG_task_irecv(&(received_task),mailbox);
	res = MSG_comm_wait(msg_recv,-1);
	if(res == MSG_OK){
	    char task_name[100];
	    strcpy(task_name,MSG_task_get_name(received_task));
	    task_data *received_task_data = (task_data *) received_task->data;
	    XBT_INFO("received task %lf",received_task_data->id);
	   // double endtime = MSG_get_clock();
	   // write_log(received_task_data->starttime,endtime,"waiting time");
	   //received_task_data->starttime = endtime;
	   int index_method = find_method(list_methods_len,list_methods,task_name);
	    chor_method_data method_data = list_methods[index_method];
	    int i;
	    for(i = 0;i<method_data.instructions_len;i++){
		method_instruction instruction = method_data.instructions[i];
		if(instruction.type == INVOKE){
		    service_invoke(instruction.role,instruction.method_name,instruction.input_size,sender_name);
		    waiting_count++;
		}else if(instruction.type == INVOKE_A){
		    service_invoke_cordel(instruction.role,instruction.method_name,instruction.input_size,received_task_data);
		}else if(instruction.type == EXEC){
		    run_chor_task(task_name,method_data);
		    }
		else if(instruction.type == WAIT){
		    wait_task(sender_name,waiting_count);
		    waiting_count = 0;
		    }
		else if(instruction.type == RETURN){
		    return_task(task_name,received_task,method_data.output_size);
		}
	    }
	}
	received_task = NULL;
    }
}
int service_invoke_cordel(char role[],char method_name[],double size,task_data *data){
	msg_comm_t msg_send;

	m_task_t task =  MSG_task_create(method_name,0,size,NULL);
	task->data = data;
	msg_send = MSG_task_isend(task,role);

	task_data data_to_print = *(task_data *)task->data;
	XBT_INFO("sending task %lf to %s",data_to_print.id,role);
	//double endtime = MSG_get_clock();
	//write_log(data_received.starttime,endtime);
}


int service_chor_controller(int argc,char *argv[]){
    int count = argc-1;
    char **arg=(char **)calloc(count,sizeof(char*));
    int process_number = atoi(argv[1]);
    m_process_t process_list[process_number];
    arg[0] = strdup("service_chor");
    int i;
    for(i=2;i<argc;i++){
	arg[i-1] = strdup(argv[i]);
    }
    
    XBT_INFO("initializing service: %s",arg[1]);

    //create a number of process
    for(i=0;i<process_number;i++){
	process_list[i] = MSG_process_create_with_arguments(arg[0],service_chor,NULL,MSG_host_self(),count,arg);
    }
    MSG_process_sleep(MAX_TIME_SIMULATION);    
    //kill all process in process_list
    for(i=0;i<process_number;i++){
	MSG_process_kill(process_list[i]);
    }
}

int service_chor(int argc,char *argv[]){
    chor_method_data list_methods[12];
    int list_methods_len = 0;
    char mailbox[50];
    strcpy(mailbox,argv[1]);
    XBT_INFO("created service %s",mailbox);
    int count_arg = 2;
    while(count_arg < argc){
	//add method
	if(strcmp(argv[count_arg],"method") == 0){
	    count_arg++;
	    make_chor_method(&list_methods[list_methods_len],argv[count_arg],argv[count_arg+1],argv[count_arg+2]);

	    XBT_INFO("method: %s",argv[count_arg]);
	    count_arg = count_arg+3;
	    //add instructions
	    int number_of_instruction = 0;
	    while(strcmp(argv[count_arg],"method") != 0){
		if(strcmp(argv[count_arg],"invoke") == 0){
		    make_instruction(&list_methods[list_methods_len].instructions[number_of_instruction],INVOKE,argv[count_arg+1],argv[count_arg+2],atof(argv[count_arg+3]));
		    count_arg = count_arg+4;
		
		}else if(strcmp(argv[count_arg],"exec") == 0){
			list_methods[list_methods_len].instructions[number_of_instruction].type = EXEC;
			count_arg++;
		}else if(strcmp(argv[count_arg],"wait") == 0){
			list_methods[list_methods_len].instructions[number_of_instruction].type = WAIT;
			count_arg++;
		    }else{	
			XBT_WARN("parser arguments error");
			return 1;}
		number_of_instruction++;
		if(count_arg >= argc){
		    break;
		}
	    }
	    list_methods[list_methods_len].instructions_len = number_of_instruction;
	    list_methods_len++;
	}
    }
    
    run_service(list_methods_len,list_methods,mailbox);
}

void run_service(int list_methods_len,chor_method_data list_methods[],char mailbox[]){
    m_task_t received_task = NULL;
    MSG_error_t res = MSG_OK;
    msg_comm_t msg_recv = NULL;
    static int id_sender = 0;
    char sender_name[50];
    sprintf(sender_name,"%s_%d",mailbox,id_sender++);
    int waiting_count = 0;
    while(1){
	msg_recv = MSG_task_irecv(&(received_task),mailbox);
	res = MSG_comm_wait(msg_recv,-1);
	if(res == MSG_OK){
	    char task_name[100];
	    strcpy(task_name,MSG_task_get_name(received_task));
	    task_data *received_task_data = (task_data *) received_task->data;
	    XBT_INFO("received task %lf",received_task_data->id);
	   // double endtime = MSG_get_clock();
	   // write_log(received_task_data->starttime,endtime,"waiting time");
	   //received_task_data->starttime = endtime;
	   int index_method = find_method(list_methods_len,list_methods,task_name);
	    chor_method_data method_data = list_methods[index_method];
	    int i;
	    for(i = 0;i<method_data.instructions_len;i++){
		method_instruction instruction = method_data.instructions[i];
		if(instruction.type == INVOKE){
		    service_invoke(instruction.role,instruction.method_name,instruction.input_size,sender_name);
		    waiting_count++;
		}else if(instruction.type == EXEC){
		    run_chor_task(task_name,method_data);
		    }
		else if(instruction.type == WAIT){
		    wait_task(sender_name,waiting_count);
		    waiting_count = 0;
		    }
	    }
	    return_task(task_name,received_task,method_data.output_size);
	}
	received_task = NULL;
    }

}

int wait_task(char sender_name[],int waiting_count){
	int i;	
	for(i = 0;i< waiting_count;i++){	
	    task_data data_received = receive_task(sender_name);
	    XBT_INFO("received task %lf", data_received.id); 
	}
}
int service_invoke(char role[],char method_name[],double size,char sender_name[]){
	msg_comm_t msg_send;

	m_task_t task =  MSG_task_create(method_name,0,size,NULL);
	task->data = xbt_new(task_data,1);

	create_task_data((task_data *) task->data,task_id++,sender_name);

	msg_send = MSG_task_isend(task,role);

	task_data data_to_print = *(task_data *)task->data;
	XBT_INFO("sending task %lf to %s",data_to_print.id,role);
	//double endtime = MSG_get_clock();
	//write_log(data_received.starttime,endtime);
}

int find_method(int list_methods_len,chor_method_data list_methods[],char task_name[]){
    int i;
    for(i=0;i<list_methods_len;i++){
	if(strcmp(task_name,list_methods[i].method) == 0)
	    return i;
    }
    XBT_WARN("method %s not found",task_name);
    return -1;
}

void run_chor_task(char task_name[],chor_method_data method_data){
    m_task_t exec_task = NULL;
    exec_task = MSG_task_create(method_data.method,method_data.exec_time,0,0);
    XBT_INFO("performing task %s", method_data.method);
    //double init_exec=MSG_get_clock();
    MSG_task_execute(exec_task);
    //write_log(init_exec,MSG_get_clock(),"exec time ");
    XBT_INFO("performed task %s", method_data.method);
    MSG_task_destroy(exec_task);
}

//create a struct method_data
void make_chor_method(chor_method_data *method_data,char method[], char exec_time[], char output_size[]){
    strcpy(method_data->method,method);
    method_data->exec_time = atof(exec_time);
    method_data->output_size = atof(output_size);
    method_data->instructions_len = 0;
}

void make_instruction(method_instruction *instruction, int type,char role[], char method_name[], double input_size){
    instruction->type = type;
    strcpy(instruction->role,role);
    strcpy(instruction->method_name,method_name);
    instruction->input_size = input_size;
}

int service(int argc, char *argv[]){
    //init list_methods
    method_data list_methods[50];
    int list_methods_len = 0; 
    
    //set mailbox
    char mailbox[50];
    strcpy(mailbox,argv[1]);

    //create the list of methods 
    int i;
    for(i = 2;i<argc;i = i+3){
	make_method(&list_methods[list_methods_len++],argv[i],argv[i+1],argv[i+2]);
    }
   
    m_task_t received_task = NULL;
    MSG_error_t res = MSG_OK;
    msg_comm_t msg_recv = NULL;
    //receive request
    while(1){
	msg_recv = MSG_task_irecv(&(received_task),mailbox);
	res = MSG_comm_wait(msg_recv,-1);
	if(res == MSG_OK){
	    char task_name[100];
	    strcpy(task_name,MSG_task_get_name(received_task));
	    task_data *received_task_data = (task_data *) received_task->data;
	    //received_task_data->starttime = MSG_get_clock();
	    //double endtime = MSG_get_clock();
	    //write_log(received_task_data->starttime,endtime,"waiting time");
	    //received_task_data->starttime = endtime;
	    XBT_INFO("received task %lf",received_task_data->id);
	    run_task(task_name,list_methods_len,list_methods,received_task);
	}
	MSG_task_destroy(received_task);
	received_task = NULL;
    }
}


void run_task(char task_name[],int list_methods_len,method_data list_methods[],m_task_t received_task){
    m_task_t exec_task = NULL;
    int i;
    for(i=0;i<list_methods_len;i++){
	if(strcmp(task_name,list_methods[i].method) == 0){
	    exec_task = MSG_task_create(list_methods[i].method,list_methods[i].exec_time,0,0);
	    XBT_INFO("performing task %s", list_methods[i].method);
	    //double init_exec=MSG_get_clock();
	    MSG_task_execute(exec_task);
	    //write_log(init_exec,MSG_get_clock(),"exec time");
	    XBT_INFO("performed task %s", list_methods[i].method);
	    MSG_task_destroy(exec_task);
	    return_task(task_name,received_task,list_methods[i].output_size);
	    break;
	}
    }
}

void return_task(char task_name[],m_task_t received_task,double output_size){
    m_task_t task = NULL;
    task_data *received_task_data = (task_data *) received_task->data;
    //write_log(received_task_data->starttime,MSG_get_clock(),"process time");
    char mailbox[100];
    strcpy(mailbox,received_task_data->sender);
    task = MSG_task_create(task_name,0,output_size,NULL);
    task->data = received_task_data;
    XBT_INFO("sending task %lf response",received_task_data->id);
    MSG_task_isend(task,mailbox);
}

//create a struct method_data
void make_method(method_data *data,char method[], char exec_time[], char output_size[]){
    strcpy(data->method,method);
    data->exec_time = atof(exec_time);
    data->output_size = atof(output_size);
}

int broker_controller(int argc, char *argv[]){
    int count = argc-1;
    char **arg=(char **)calloc(count,sizeof(char*));
    int process_number = atoi(argv[1]);
    m_process_t process_list[process_number];
    arg[0] = strdup("broker");
	int i;
    
    for(i=2;i<argc;i++){
	arg[i-1]= strdup(argv[i]);
    }
    //create a number of process
    for(i=0;i<process_number;i++){
	process_list[i] = MSG_process_create_with_arguments(arg[0],broker,NULL,MSG_host_self(),count,arg);
	if ((i+1) >= atoi(argv[2])  && (((i+1) % (atoi(argv[2]))) == 0))
	    MSG_process_sleep(1);
    }
    MSG_process_sleep(MAX_TIME_SIMULATION);    
    //kill all process
    for(i=0;i<process_number;i++){
//	MSG_process_kill(process_list[i]);
    }
}

int broker(int argc, char *argv[]){
    //set number of request
    int requests_per_second= atoi(argv[1]);
    double static broker_id = 0;
    request list_requests[50]; 
    int list_requests_len = 0;

    //set mailbox
    char mailbox[50];
    sprintf(mailbox,"%s_%lf",argv[0],broker_id++);

    msg_comm_t msg_send;
   //create list of requests
    int i;
    for( i = 2;i < argc;i = i+3){
	make_request(&list_requests[list_requests_len++],argv[i],argv[i+1],argv[i+2]);
    }

    //send requests
    int j;
    for(j =0;j < list_requests_len;j++){
	m_task_t task =  MSG_task_create(list_requests[j].method,0,
		list_requests[j].input_size,NULL);
	task->data = xbt_new(task_data,1);

	create_task_data((task_data *) task->data,task_id++,mailbox);

	msg_send = MSG_task_isend(task,list_requests[j].role);

	task_data data_to_print = *(task_data *)task->data;
	XBT_INFO("sending task %lf to %s",data_to_print.id,list_requests[j].role );
	
     //receive response
	task_data data_received = receive_task(mailbox);
	XBT_INFO("received task %lf", data_received.id); 
	double endtime = MSG_get_clock();
	write_log(data_received.starttime,endtime,"time_total");
    }
  XBT_INFO("ending choreography"); 
}

void write_log(double starttime, double endtime,char text[]){
    double response_time = endtime-starttime;
    printf("%lf %lf %s\n",endtime,(endtime-starttime),text);
}

void create_task_data(task_data *data,double task_id,char sender[]){
    data->id = task_id;
    data->starttime = MSG_get_clock();
    strcpy(data->sender,sender);
}

task_data receive_task(char mailbox[]){
    m_task_t task = NULL;
    msg_comm_t res = NULL;
    res = MSG_task_irecv(&(task),mailbox);
    MSG_comm_wait(res,-1);
    task_data data = *(task_data *) task->data;
    XBT_INFO("received response task %s",MSG_task_get_name(task));
    //MSG_comm_destroy(res);
    //MSG_task_destroy(task);
    return data;
}


void make_request(request *r,char role[],char method[],char input_size[]){
    strcpy(r->role,role);
    strcpy(r->method,method);
	r->input_size = atof(input_size);
}


MSG_error_t run(const char *platform_file,const char *application_file)
{

    MSG_error_t res = MSG_OK;

    XBT_INFO("running simulation");

    /*  Simulation setting */
    MSG_create_environment(platform_file);

    /*   Application deployment */
    MSG_function_register("service_cordel",service_cordel);
    MSG_function_register("service_chor",service_chor);
    MSG_function_register("service", service);
    MSG_function_register("broker",broker);
    MSG_function_register("service_controller",service_controller);
    MSG_function_register("broker_controller",broker_controller);
    MSG_function_register("service_chor_controller",service_chor_controller);
    MSG_function_register("service_cordel_controller",service_cordel_controller);
    MSG_launch_application(application_file);


    res = MSG_main();

    return res;
}/* end_of_test_all */



int main(int argc, char *argv[])
{   
    MSG_error_t res = MSG_OK;
    MSG_global_init(&argc, argv);


    if (argc != 3) {
        XBT_CRITICAL("Usage: %s platform_file deployment_file \n",
                argv[0]);
        XBT_CRITICAL
            ("example: %s msg_platform.xml msg_deployment.xml \n",
             argv[0]);
        exit(1);
    }
    res = run(argv[1], argv[2]);

    XBT_INFO("Total simulation time: %le", MSG_get_clock());

    MSG_clean();


    if (res == MSG_OK)
        return 0;
    else
        return 1;
} 
