#include<stdio.h>
#include<cstring>

int test(){
    char* a = "abc";
    char* b = "bcd";
    char* c = "cdc";

    char* cmp = "a";
    if(strncmp(cmp,a,2) == 0){
        return 0;
    }
    else if(strncmp(cmp,b,2) == 0){
        return 0;
    }
    else if(strncmp(cmp,c,2) == 0){
        return 0;
    }
    else{
        return 1;
    }
}

int main(){
    test();
    return 0;
}