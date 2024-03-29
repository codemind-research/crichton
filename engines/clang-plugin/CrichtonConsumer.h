#pragma once

#include "clang/AST/ASTConsumer.h"
#include "clang/AST/ASTContext.h"
#include "clang/ASTMatchers/ASTMatchFinder.h"
#include "clang/Basic/LangOptions.h"
#include "clang/Basic/SourceManager.h"
#include "clang/Frontend/CompilerInstance.h"

#include <map>
#include <memory>
#include <set>
#include <string>

#include "CrichtonItems.h"

namespace Crichton {
  using namespace std;
  using namespace llvm;
  using namespace clang;
  using namespace ast_matchers;

  struct Option {
    string output;
  };

  class CrichtonASTConsumer : public ASTConsumer {
    private:
      Option option;
      MatchFinder finder;
      const CompilerInstance &compiler;
      vector<shared_ptr<MatchFinder::MatchCallback>> callbacks;
      unique_ptr<CrichtonItems> items;
    public:
      CrichtonASTConsumer(const CompilerInstance &ci, Option& opt) : compiler(ci) {
        option = opt;
        items = make_unique<CrichtonItems>(ci);
      }

      void HandleTranslationUnit(ASTContext &ctx) override;

      virtual MatchFinder &getMatchFinder() {
        return finder;
      }

      const CompilerInstance &getCompilerInstance() {
        return compiler;
      }

      SourceManager &getSourceManager() {
        return compiler.getSourceManager();
      }

      ASTContext &getASTContext() {
        return compiler.getASTContext();
      }

      const LangOptions &getLangOpts() {
        return compiler.getLangOpts();
      }

      PrintingPolicy getPrintingPolicy() {
        return getASTContext().getPrintingPolicy();
      }

      void addMatcher(shared_ptr<MatchFinder::MatchCallback> callback) {
        callbacks.push_back(callback);
      }

      CrichtonItems *getCrichtonItems() {
        return items.get();
      }
  };
}