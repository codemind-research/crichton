/*
  clang -Xclang -load -Xclang ./libcrichton.so -Xclang -plugin -Xclang crichton -Xclang [-plugin-arg-crichton -Xclang -help] -c {source file}
*/

#include "clang/Basic/Diagnostic.h"
#include "clang/Frontend/FrontendPluginRegistry.h"

#include <sstream>

#include "CrichtonAction.h"
#include "CrichtonConsumer.h"

using namespace Crichton;

/// plugin 등록
static FrontendPluginRegistry::Add<CrichtonAction> X("crichton", "Print the names of functions inside the file.");

unique_ptr<ASTConsumer> CrichtonAction::CreateASTConsumer(CompilerInstance &ci, StringRef InFile) {
  if (option.output.empty()) {
    SmallString<128> ReplaceName(ci.getFrontendOpts().OutputFile);
    llvm::sys::path::replace_extension(ReplaceName, ".output");
    option.output = ReplaceName.c_str();
  }
  return make_unique<CrichtonASTConsumer>(ci, option);
}

bool CrichtonAction::ParseArgs(const CompilerInstance &ci, const vector<string>& args) {
  bool show_err = false;
  auto lang = ci.getLangOpts();
  for (unsigned i = 0, size = args.size(); i < size; i++) {
    string opt;
    stringstream arg(args[i]);
    getline(arg, opt, '=');

    if (opt == "-show-error")
      show_err = true;
    else if (opt == "-output") {
      getline(arg, option.output);
    } else if (opt == "-help") {
      printHelp();
      return false;
    }
  }
  if (!show_err)
    ci.getDiagnostics().setClient(new IgnoringDiagConsumer());

  return true;
}

void CrichtonAction::printHelp() {
  errs() << "Help for Crichton plugin goes here\n";
}

