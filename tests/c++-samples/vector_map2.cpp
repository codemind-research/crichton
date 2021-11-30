#include <iostream>
#include <vector>
#include <string>
#include <map>
 
using namespace std;

int main()
{
    std::vector<int> vt; 
 
    vt.push_back(1);
    vt.push_back(2);
    vt.push_back(3);
 
    std::vector<int>::iterator iter;
    for (iter = vt.begin(); iter != vt.end() ; iter++)
        std::cout << "vector : " << *iter << std::endl;
 
    iter = vt.begin();
    std::cout << iter[0] << std::endl;
    std::cout << iter[1] << std::endl;
 
    iter += 2;
    std::cout << *iter << std::endl;
 
    map<string, int> map;

    map.insert(pair<string, int>("first", 1));
    map.insert(pair<string, int>("second", 2));
    map.insert(pair<string, int>("third", 3));
    for (auto iter : map) {
        cout << iter.first << " " << iter.second << endl;
    }

    map.erase(map.find("2"));

    return 0;
}
