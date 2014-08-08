package natlab.backends.vrirGen;

import java.util.Set;

import natlab.tame.tamerplus.analysis.AnalysisEngine;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.ValueFlowMap;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import nodecases.natlab.NatlabAbstractNodeCaseHandler;
import ast.ASTNode;
import ast.AndExpr;
import ast.AssignStmt;
import ast.BreakStmt;
import ast.CellArrayExpr;
import ast.CellIndexExpr;
import ast.ColonExpr;
import ast.ContinueStmt;
import ast.DotExpr;
import ast.ElseBlock;
import ast.EmptyStmt;
import ast.Expr;
import ast.FPLiteralExpr;
import ast.ForStmt;
import ast.Function;
import ast.FunctionList;
import ast.IfBlock;
import ast.IfStmt;
import ast.IntLiteralExpr;
import ast.List;
import ast.MatrixExpr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.RangeExpr;
import ast.ReturnStmt;
import ast.Row;
import ast.ShortCircuitAndExpr;
import ast.Stmt;
import ast.StringLiteralExpr;
import ast.SuperClassMethodExpr;
import ast.SwitchStmt;
import ast.TryStmt;
import ast.WhileStmt;

public class VrirXmlGen extends NatlabAbstractNodeCaseHandler {

	private StringBuffer prettyPrintedCode = null;
	// private StringBuffer bodyCode;
	private SymbolTable symTab;
	private Set<String> remainingVars;
	private ValueAnalysis<AggrValue<BasicMatrixValue>> analysis;
	private ValueFlowMap<AggrValue<BasicMatrixValue>> currentOutSet;
	private int size;
	private int index;
	final static public boolean onGPU = false;
	private AnalysisEngine analysisEngine;
	private Function functionNode;
	private boolean hasReturnStmt = false;

	VrirXmlGen(Function functionNode, Set<String> remainVars,
			ValueAnalysis<AggrValue<BasicMatrixValue>> analysis,
			ValueFlowMap<AggrValue<BasicMatrixValue>> currentOutSet, int size,
			int index, AnalysisEngine analysisEngine) {
		prettyPrintedCode = new StringBuffer();
		remainingVars = remainVars;
		this.analysis = analysis;
		this.currentOutSet = currentOutSet;
		this.size = size;
		this.index = index;
		symTab = new SymbolTable();
		// indent = 0;
		this.analysisEngine = analysisEngine;
		this.functionNode = functionNode;
		functionNode.analyze(this);

	}

	public Function getFunctionNode() {
		return functionNode;
	}

	public void setFunctionNode(Function functionNode) {
		this.functionNode = functionNode;
	}

	public static void genModuleXMLHead(StringBuffer target, String moduleName) {
		target.append("<module name=\"" + moduleName + "\">\n");

	}

	public static void genModuleXMLTail(StringBuffer target) {
		target.append("</module>\n");
	}

	public void addToSymTab(VType vtype, String name) {
		symTab.putSymbol(vtype, name);
	}

	public Symbol getSymbol(String name) {
		return symTab.getSymbol(name);
	}

	public void appendToPrettyCode(StringBuffer buff) {
		prettyPrintedCode.append(buff);
	}

	public void appendToPrettyCode(String buff) {
		prettyPrintedCode.append(buff);
	}

	public Set<String> getRemainingVars() {
		return remainingVars;
	}

	public void setRemainingVars(Set<String> remainingVars) {
		this.remainingVars = remainingVars;
	}

	public ValueAnalysis<AggrValue<BasicMatrixValue>> getAnalysis() {
		return analysis;
	}

	public void setAnalysis(ValueAnalysis<AggrValue<BasicMatrixValue>> analysis) {
		this.analysis = analysis;
	}

	public ValueFlowMap<AggrValue<BasicMatrixValue>> getCurrentOutSet() {
		return currentOutSet;
	}

	public void setCurrentOutSet(
			ValueFlowMap<AggrValue<BasicMatrixValue>> currentOutSet) {
		this.currentOutSet = currentOutSet;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setPrettyPrintedCode(StringBuffer prettyPrintedCode) {
		this.prettyPrintedCode = prettyPrintedCode;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void caseASTNode(ASTNode node) {
		System.out.println("unsupported ast node" + node.getClass());
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void caseList(List node) {

		caseASTNode(node);
	}

	@Override
	public void caseExpr(Expr node) {
		caseASTNode(node);
	}

	public void caseIfBlock(IfBlock node) {
		caseASTNode(node);
	}

	public void caseElseBlock(ElseBlock node) {
		caseASTNode(node);
	}

	public void caseRow(Row node) {
		caseASTNode(node);
	}

	public void caseFunctionList(FunctionList node) {
		caseProgram(node);
	}

	public void caseFunction(Function node) {
		// caseFunctionOrSignatureOrPropertyAccessOrStmt(node);
		FunctionCaseHandler.handleHeader(node, this);
		this.appendToPrettyCode(HelperClass.toXML("body"));
		this.appendToPrettyCode(StmtCaseHandler.toListXMLHead(false));
		for (Stmt stmt : node.getStmts()) {

			stmt.analyze(this);
		}
		if (node.getOutputParamList().getNumChild() > 0) {
			if (!hasReturnStmt) {

				this.appendToPrettyCode(StmtCaseHandler.toXMLHead("returnstmt"));
				this.appendToPrettyCode(HelperClass.toXML("exprs"));
				for (Name rvar : this.getFunctionNode().getOutputParamList()) {
					if (!this.getSymTab().contains(rvar.getID())) {
						VType vtype = HelperClass.generateVType(
								this.getAnalysis(), this.getIndex(),
								rvar.getID());
						this.getSymTab().putSymbol(vtype, rvar.getID());
					}
					if (this.getSymbol(rvar.getID()) != null) {
						this.appendToPrettyCode(ExprCaseHandler.toXMLHead(
								"name", this.getSymbol(rvar.getID()).getId(),
								"id"));
					} else {
						throw new NullPointerException("Symbol not found for "
								+ rvar.getID());
					}
					this.appendToPrettyCode(this.getSymbol(rvar.getID())
							.getVtype().toXML());
					this.appendToPrettyCode(ExprCaseHandler.toXMLTail());
				}

				this.appendToPrettyCode(HelperClass.toXML("/exprs"));
				this.appendToPrettyCode(StmtCaseHandler.toXMLTail());
			}
		}
		this.appendToPrettyCode(StmtCaseHandler.toListXMLTail());
		this.appendToPrettyCode(HelperClass.toXML("/body"));

		this.appendToPrettyCode(symTab.toXML());

		FunctionCaseHandler.handleTail(node, this);
		this.setHasReturnStmt(false);

	}

	public boolean isHasReturnStmt() {
		return hasReturnStmt;
	}

	public void setHasReturnStmt(boolean hasReturnStmt) {
		this.hasReturnStmt = hasReturnStmt;
	}

	public void caseAssignStmt(AssignStmt node) {

		StmtCaseHandler.handleAssignStmt(node, this);

		// caseStmt(node);

	}

	public void caseBreakStmt(BreakStmt node) {
		StmtCaseHandler.handleBreakStmt(node, this);
		caseStmt(node);
	}

	public void caseContinueStmt(ContinueStmt node) {
		StmtCaseHandler.handleContinueStmt(node, this);
		caseStmt(node);
	}

	public void caseReturnStmt(ReturnStmt node) {

		StmtCaseHandler.handleReturnStmt(node, this);

		// caseStmt(node);
	}

	public void caseEmptyStmt(EmptyStmt node) {
		caseStmt(node);
	}

	public void caseForStmt(ForStmt node) {
		StmtCaseHandler.handleForStmt(node, this);
		// caseStmt(node);
	}

	public void caseWhileStmt(WhileStmt node) {

		StmtCaseHandler.handleWhileStmt(node, this);
		// caseStmt(node);
	}

	public void caseTryStmt(TryStmt node) {
		caseStmt(node);
	}

	public void caseSwitchStmt(SwitchStmt node) {

		caseStmt(node);
	}

	public void caseIfStmt(IfStmt node) {
		StmtCaseHandler.handleIfStmt(node, this);

		// caseStmt(node);
	}

	public void caseRangeExpr(RangeExpr node) {
		ExprCaseHandler.handleRangeExpr(node, this);
		// caseExpr(node);
	}

	public void caseColonExpr(ColonExpr node) {
		ExprCaseHandler.handleColonExpr(node, this);
		caseExpr(node);
	}

	public void caseNameExpr(NameExpr node) {

		ExprCaseHandler.handleNameExpr(node, this);
	}

	public void caseParameterizedExpr(ParameterizedExpr node) {

		ExprCaseHandler.handleParameterizedExpr(node, this);
		// caseLValueExpr(node);
	}

	public SymbolTable getSymTab() {
		return symTab;
	}

	public void setSymTab(SymbolTable symTab) {
		this.symTab = symTab;
	}

	public void caseCellIndexExpr(CellIndexExpr node) {
		caseLValueExpr(node);
	}

	public void caseDotExpr(DotExpr node) {
		caseLValueExpr(node);
	}

	public void caseMatrixExpr(MatrixExpr node) {
		ExprCaseHandler.handleMatrixExpr(node, this);
	}

	public void caseCellArrayExpr(CellArrayExpr node) {

		caseExpr(node);
	}

	public void caseSuperClassMethodExpr(SuperClassMethodExpr node) {
		caseExpr(node);
	}

	public AnalysisEngine getAnalysisEngine() {
		return analysisEngine;
	}

	public void setAnalysisEngine(AnalysisEngine analysisEngine) {
		this.analysisEngine = analysisEngine;
	}

	public void caseIntLiteralExpr(IntLiteralExpr node) {

		ExprCaseHandler.handleIntLiteralExpr(node, this);
		// caseLiteralExpr(node);
	}

	public void caseFPLiteralExpr(FPLiteralExpr node) {
		ExprCaseHandler.handleFpLiteralExpr(node, this);
		// caseLiteralExpr(node);
	}

	public void caseShortCircuitAndExpr(ShortCircuitAndExpr node) {
		ExprCaseHandler.handleShortCircuitAndExpr(node, this);
	}

	public void caseStringLiteralExpr(StringLiteralExpr node) {
		ExprCaseHandler.handleStringLiteralExpr(node, this);
		// caseLiteralExpr(node);
	}

	public static StringBuffer generateVrir(Function functionNode,
			Set<String> remainingVars,
			ValueAnalysis<AggrValue<BasicMatrixValue>> analysis,
			ValueFlowMap<AggrValue<BasicMatrixValue>> currentOutSet, int index,
			int size, AnalysisEngine analysisEngine) {
		return (new VrirXmlGen(functionNode, remainingVars, analysis,
				currentOutSet, size, index, analysisEngine))
				.getPrettyPrintedCode();
	}

	public StringBuffer getPrettyPrintedCode() {
		return prettyPrintedCode;
	}

}
