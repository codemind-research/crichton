struct str{
    int a;
    int c;
};
class A{
    public : 
    int a;
    int b;
};

union C{
    int a;
    int b;
};

int fun1(){
    A *a = 0;
    a->a = 10;
    return 0;
}

int fun2(){
    C *a = 0;
    a->a = 10;
    return 0;
}

int main(){
    str *_str = 0;
    _str->a = 10;
    return 0;    
}