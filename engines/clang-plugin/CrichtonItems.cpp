#include <fstream>

#include "CrichtonItems.h"

using namespace Crichton;

void CrichtonItems::writeProto(string path) {
  if (!path.empty()) {
    fstream output(path, ios::out | ios::trunc | ios::binary);
    if (!proto.SerializeToOstream(&output))
      errs() << "Failed to write of file\n";
  } else
    errs() << "Failed to write\n";
}

void CrichtonItems::addFunction(const FunctionDecl *fd) {
  proto.add_name(fd->getQualifiedNameAsString());
}