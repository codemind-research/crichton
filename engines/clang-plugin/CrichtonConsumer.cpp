#include "CrichtonConsumer.h"

using namespace Crichton;

class CrichtonMatcher : public MatchFinder::MatchCallback {
  protected:
    CrichtonASTConsumer *consumer;
  public:
    CrichtonMatcher(CrichtonASTConsumer *cs) : consumer(cs) {
    }

    SourceManager &getSourceManager() {
      return consumer->getSourceManager();
    }
};

class FunctionMatcher : public CrichtonMatcher {
  public:
    FunctionMatcher(CrichtonASTConsumer *cs) : CrichtonMatcher(cs) {}
    void run(const MatchFinder::MatchResult &Results) override {
      auto fd = Results.Nodes.getNodeAs<FunctionDecl>("function");
      outs() << fd->getQualifiedNameAsString() << "\n";
    }
};

void CrichtonASTConsumer::HandleTranslationUnit(ASTContext &ctx) {
  auto codegenFunction = make_shared<FunctionMatcher>(this);
  addMatcher(codegenFunction);
  finder.addMatcher(
    functionDecl().bind("function"),
    codegenFunction.get());

  finder.matchAST(ctx);
}