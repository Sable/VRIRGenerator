package natlab.backends.vrirGen;
/*
 * package natlab.backends.VRIRGen;
 * 
 * import java.util.ArrayList; import java.util.HashMap; import java.util.List;
 * 
 * import natlab.tame.valueanalysis.ValueAnalysis; import
 * natlab.tame.valueanalysis.ValueFlowMap; import
 * natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValue; import
 * natlab.tame.valueanalysis.aggrvalue.AggrValue; import
 * natlab.tame.valueanalysis.components.shape.DimValue; import
 * natlab.tame.valueanalysis.components.shape.Shape; import
 * analysis.AbstractDepthFirstAnalysis; import ast.ASTNode; import ast.Expr;
 * import ast.IntLiteralExpr; import ast.NameExpr; import ast.ParameterizedExpr;
 * 
 * @SuppressWarnings("rawtypes") public class ExprTypeAnalyzer extends
 * AbstractDepthFirstAnalysis<HashMap<Expr, VType>> { private HashMap<Expr,
 * VType> exprType; private ValueAnalysis<AggrValue<AdvancedMatrixValue>>
 * analysis; private int graphIndex;
 * 
 * public HashMap<Expr, VType> newInitialFlow() { return new HashMap<Expr,
 * VType>(); }
 * 
 * public HashMap<Expr, VType> getExprType() { return exprType; }
 * 
 * public void setExprType(HashMap<Expr, VType> exprType) { this.exprType =
 * exprType; }
 * 
 * public VType getVType(Expr node) { return exprType.get(node); }
 * 
 * public ExprTypeAnalyzer(ASTNode tree,
 * ValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis,
 * ValueFlowMap<AggrValue<AdvancedMatrixValue>> currentOutSet, int graphIndex) {
 * this.analysis = analysis; exprType = newInitialFlow(); this.graphIndex =
 * graphIndex; this.analyze(tree); }
 * 
 * public void caseASTNode(ASTNode node) {
 * 
 * for (int i = 0; i < node.getNumChild(); i++) {
 * node.getChild(i).analyze(this); }
 * 
 * }
 * 
 * public void caseParameterizedExpr(ParameterizedExpr node) {
 * System.out.println("entered parameterized expr"); if
 * (OperatorMapper.isOperator(node.getVarName())) { if
 * (!exprType.containsKey(node)) { if (node.getArgList().getNumChild() == 2) {
 * handleBinaryExpr(node); } else if (node.getArgList().getNumChild() == 1) {
 * handleUnaryExpr(node); } } } }
 * 
 * public void caseIntLiteralExpr(IntLiteralExpr node) { // Vtype vtype=new
 * Vtype(null, // PrimitiveClassReference.INT32,VType.Layout.ROW_MAJOR,) }
 * 
 * public void handleBinaryExpr(ParameterizedExpr node) { if
 * (!exprType.containsKey(node.getArg(0))) { node.getArg(0).analyze(this); }
 * VType lhsType = exprType.get(node.getArg(0)); if
 * (!exprType.containsKey(node.getArg(1))) { node.getArg(1).analyze(this); }
 * VType rhsType = exprType.get(node.getArg(1)); VType outputType =
 * getOutputVType(lhsType, rhsType); exprType.put(node, outputType); }
 * 
 * public void handleUnaryExpr(ParameterizedExpr node) { if
 * (!exprType.containsKey(node.getArg(0))) { node.getArg(0).analyze(this); }
 * exprType.put(node, exprType.get(node.getArg(0))); }
 * 
 * public VType getOutputVType(VType lhs, VType rhs) { List<DimValue> outList =
 * new ArrayList<DimValue>(); int minDim =
 * Math.min(lhs.getShape().getDimensions().size(), rhs
 * .getShape().getDimensions().size()); int i = 0; for (i = 0; i < minDim; i++)
 * {
 * 
 * outList.add(lhs.getShape().getDimensions().get(i).getIntValue() > rhs
 * .getShape().getDimensions().get(i).getIntValue() ? lhs
 * .getShape().getDimensions().get(i) : rhs.getShape() .getDimensions().get(i));
 * } for (int j = i; j < lhs.getShape().getDimensions().size(); j++) {
 * outList.add(lhs.getShape().getDimensions().get(j)); } for (int j = i; j <
 * rhs.getShape().getDimensions().size(); j++) {
 * outList.add(rhs.getShape().getDimensions().get(j)); }
 * Shape<AggrValue<AdvancedMatrixValue>> outShape = new
 * Shape<AggrValue<AdvancedMatrixValue>>( outList);
 * 
 * return new VTypeMatrix(outShape, lhs.getType(), lhs.getLayout(),
 * lhs.getComplexity()); }
 * 
 * public void caseNameExpr(NameExpr node) { VType vtype =
 * HelperClass.generateVType(analysis, getGraphIndex(), node.getName());
 * exprType.put(node, vtype); }
 * 
 * public int getGraphIndex() { return graphIndex; }
 * 
 * public void setGraphIndex(int graphIndex) { this.graphIndex = graphIndex; }
 * 
 * }
 */