#include "CrichtonConsumer.h"

using namespace Crichton;

void CrichtonASTConsumer::HandleTranslationUnit(ASTContext &ctx) {
  outs() << "11111111\n";
  finder.matchAST(ctx);
}