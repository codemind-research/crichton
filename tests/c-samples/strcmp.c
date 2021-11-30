#include<string.h>
int test(char* a, char* b, char * c){

    if(strcmp(a, "a") == 0 || strcmp(b, "b") == 0|| strcmp(c, "c")==0){
        return 0;
    }
    return 1;
}