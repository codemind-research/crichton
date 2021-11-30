#include <vector>
#include <map>
#include <stdio.h>
#include <string>

using namespace std;

int test(){
    vector<int> v1 = { 1, 2, 3, 4, 5};
    for(int i : v1)
        if(i > 10)
            printf("v1 length : %llu\n", v1.size());


    map<string, int> map;

    map.insert(pair<string, int>("first", 1));
    map.insert(pair<string, int>("second", 2));
    map.insert(pair<string, int>("third", 3));
    
    map.erase(map.find("2"));


    int ari[] = { 1, 2, 3, 4, 5}; 
    vector<int> v2(&ari[0],&ari[5]);
    vector<int>::iterator it;
    for (it=v2.begin(); it!=v2.end(); it++){
        printf("%d\n",*it);
    }

    return 0;
}


int test2(vector<char> test){
    for(char i : test){
        if(i == 'a')
            return 1;
    }
    return 0;
}

int main(){
    vector<char> v = {'a', 'b', 'c'};
    int i = test2(v);
    printf("%d", i);

    return 0;
}