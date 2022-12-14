#pragma once

#include "llvm/ADT/StringRef.h"
#include "clang/Frontend/FrontendActions.h"

#include <memory>
#include <string>
#include <vector>

#include "CrichtonConsumer.h"

namespace Crichton {
  using namespace std;
  using namespace llvm;
  using namespace clang;

  class CrichtonAction : public PluginASTAction {
    private:
      Option option;
    public:
      unique_ptr<ASTConsumer> CreateASTConsumer(CompilerInstance &ci, StringRef InFile) override;
      bool ParseArgs(const CompilerInstance &ci, const vector<string>& args) override;
      void printError();
      void printHelp();
  };
}