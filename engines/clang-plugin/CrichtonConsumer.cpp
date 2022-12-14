#include "CrichtonConsumer.h"

using namespace Crichton;

class CrichtonMatcher : public MatchFinder::MatchCallback {
  protected:
    CrichtonASTConsumer *consumer;
  public:
    CrichtonMatcher(CrichtonASTConsumer *cs) : consumer(cs) {}

    SourceManager &getSourceManager() {
      return consumer->getSourceManager();
    }

    CrichtonItems *getCrichtonItems() {
      return consumer != nullptr ? consumer->getCrichtonItems() : nullptr;
    }
};

class FunctionMatcher : public CrichtonMatcher {
  public:
    FunctionMatcher(CrichtonASTConsumer *cs) : CrichtonMatcher(cs) {}
    void run(const MatchFinder::MatchResult &Results) override {
      auto fd = Results.Nodes.getNodeAs<FunctionDecl>("function");
      if (auto items = getCrichtonItems())
        items->addFunction(fd);
    }
};

void CrichtonASTConsumer::HandleTranslationUnit(ASTContext &ctx) {
  auto codegenFunction = make_shared<FunctionMatcher>(this);
  addMatcher(codegenFunction);
  finder.addMatcher(
    functionDecl().bind("function"),
    codegenFunction.get());

  finder.matchAST(ctx);

  items->writeProto(option.output);
}