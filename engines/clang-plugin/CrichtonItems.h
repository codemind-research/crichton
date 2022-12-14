#pragma once

#include "clang/AST/ASTConsumer.h"
#include "clang/AST/ASTContext.h"
#include "clang/Basic/SourceManager.h"
#include "clang/Frontend/CompilerInstance.h"
#include "google/protobuf/message_lite.h"

#include "fun.pb.h"

namespace Crichton {
  using namespace std;
  using namespace llvm;
  using namespace clang;
  using namespace google;
  using namespace crichton::proto;

  class CrichtonItems {
    private:
      Fun proto;
      map<int, void*> pMap;
      const CompilerInstance &compiler;
    public:
      CrichtonItems(const CompilerInstance &ci) : compiler(ci) {}

      SourceManager &getSourceManager() {
        return compiler.getSourceManager();
      }

      ASTContext &getASTContext() {
        return compiler.getASTContext();
      }

      PrintingPolicy getPrintingPolicy() {
        return getASTContext().getPrintingPolicy();
      }

      template<typename TYPE>
      TYPE *getProtoItem(int id) {
        return reinterpret_cast<TYPE*>(pMap[id]);
      }

      void writeProto(string path);

      void addFunction(const FunctionDecl *fd);    
  };
}