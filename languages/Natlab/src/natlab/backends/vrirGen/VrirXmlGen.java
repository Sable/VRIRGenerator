package natlab.backends.vrirGen;

import java.util.Set;

import ast.ASTNode;
import ast.AndExpr;
import ast.ArrayTransposeExpr;
import ast.AssignStmt;
import ast.Attribute;
import ast.BinaryExpr;
import ast.Body;
import ast.BreakStmt;
import ast.CSLExpr;
import ast.CellArrayExpr;
import ast.CellIndexExpr;
import ast.CheckScalarStmt;
import ast.ClassBody;
import ast.ClassDef;
import ast.ClassEvents;
import ast.ColonExpr;
import ast.CompilationUnits;
import ast.ContinueStmt;
import ast.DefaultCaseBlock;
import ast.DotExpr;
import ast.EDivExpr;
import ast.ELDivExpr;
import ast.EPowExpr;
import ast.EQExpr;
import ast.ETimesExpr;
import ast.ElseBlock;
import ast.EmptyProgram;
import ast.EmptyStmt;
import ast.EndCallExpr;
import ast.EndExpr;
import ast.Event;
import ast.Expr;
import ast.ExprStmt;
import ast.FPLiteralExpr;
import ast.ForStmt;
import ast.Function;
import ast.FunctionHandleExpr;
import ast.FunctionList;
import ast.FunctionOrSignatureOrPropertyAccessOrStmt;
import ast.GEExpr;
import ast.GTExpr;
import ast.GlobalStmt;
import ast.HelpComment;
import ast.IfBlock;
import ast.IfStmt;
import ast.IntLiteralExpr;
import ast.LEExpr;
import ast.LTExpr;
import ast.LValueExpr;
import ast.LambdaExpr;
import ast.List;
import ast.LiteralExpr;
import ast.MDivExpr;
import ast.MLDivExpr;
import ast.MPowExpr;
import ast.MTimesExpr;
import ast.MTransposeExpr;
import ast.MatrixExpr;
import ast.Methods;
import ast.MinusExpr;
import ast.MultiLineHelpComment;
import ast.NEExpr;
import ast.Name;
import ast.NameExpr;
import ast.NotExpr;
import ast.OneLineHelpComment;
import ast.OrExpr;
import ast.ParameterizedExpr;
import ast.PersistentStmt;
import ast.PlusExpr;
import ast.Program;
import ast.Properties;
import ast.Property;
import ast.PropertyAccess;
import ast.RangeExpr;
import ast.ReturnStmt;
import ast.Row;
import ast.Script;
import ast.ShellCommandStmt;
import ast.ShortCircuitAndExpr;
import ast.ShortCircuitOrExpr;
import ast.Signature;
import ast.Stmt;
import ast.StringLiteralExpr;
import ast.SuperClass;
import ast.SuperClassMethodExpr;
import ast.SwitchCaseBlock;
import ast.SwitchStmt;
import ast.TryStmt;
import ast.UMinusExpr;
import ast.UPlusExpr;
import ast.UnaryExpr;
import ast.WhileStmt;
import natlab.tame.tamerplus.analysis.AnalysisEngine;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.ValueFlowMap;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValue;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import nodecases.natlab.NatlabAbstractNodeCaseHandler;

public class VrirXmlGen extends NatlabAbstractNodeCaseHandler {

	private StringBuffer prettyPrintedCode = null;
	// private StringBuffer bodyCode;
	private SymbolTable symTab;
	private Set<String> remainingVars;
	private ValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis;
	private ValueFlowMap<AggrValue<AdvancedMatrixValue>> currentOutSet;
	private int size;
	private int index;
	final static public boolean onGPU = false;
	private AnalysisEngine analysisEngine;

	VrirXmlGen(Function functionNode, Set<String> remainVars,
			ValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis,
			ValueFlowMap<AggrValue<AdvancedMatrixValue>> currentOutSet,
			int size, int index, AnalysisEngine analysisEngine) {
		prettyPrintedCode = new StringBuffer();
		remainingVars = remainVars;
		this.analysis = analysis;
		this.currentOutSet = currentOutSet;
		this.size = size;
		this.index = index;
		symTab = new SymbolTable();
		// indent = 0;
		this.analysisEngine = analysisEngine;

		functionNode.analyze(this);

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

	public ValueAnalysis<AggrValue<AdvancedMatrixValue>> getAnalysis() {
		return analysis;
	}

	public void setAnalysis(
			ValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis) {
		this.analysis = analysis;
	}

	public ValueFlowMap<AggrValue<AdvancedMatrixValue>> getCurrentOutSet() {
		return currentOutSet;
	}

	public void setCurrentOutSet(
			ValueFlowMap<AggrValue<AdvancedMatrixValue>> currentOutSet) {
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
		// System.out.println("unsupported ast node" + node.getClass());
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
		this.appendToPrettyCode(StmtCaseHandler.toListXMLTail());
		this.appendToPrettyCode(HelperClass.toXML("/body"));

		this.appendToPrettyCode(symTab.toXML());

		FunctionCaseHandler.handleTail(node, this);

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
		caseStmt(node);
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
		System.out.println("in colon expression : Parent "
				+ node.getParent().getParent());
		if (node.getParent().getParent() instanceof ParameterizedExpr) {
			ParameterizedExpr arrayExpr = (ParameterizedExpr) node.getParent()
					.getParent();
			int colonPos = Integer.MIN_VALUE;
			for (int i = 0; i < arrayExpr.getArgList().getNumChild(); i++) {
				if (arrayExpr.getArg(i) instanceof ColonExpr) {
					colonPos = i;
					break;
				}
			}
			if (colonPos == Integer.MIN_VALUE) {
				throw new RuntimeException(
						"Colon Expression not found in array");
			}
			VType vt = this.getSymbol(arrayExpr.getVarName()).getVtype();
			if (vt == null) {
				throw new NullPointerException(
						"no entry of array in symbol table");
			}
			int end;

			if (vt instanceof VTypeMatrix) {
				int ndims = ((VTypeMatrix) vt).getShape().getDimensions()
						.size();
				end = ((VTypeMatrix) vt).getShape().getDimensions()
						.get(colonPos).getIntValue();
				if (ndims > arrayExpr.getArgList().getNumChild()
						&& (colonPos == arrayExpr.getNumChild() - 1)) {
					for (int i = colonPos + 1; i < ndims; i++) {
						end *= ((VTypeMatrix) vt).getShape().getDimensions()
								.get(i).getIntValue();
					}
				}
			} else {
				throw new UnsupportedOperationException(
						"VType class is not VTypeMatrix but instead is "
								+ vt.getClass());
			}
			//TODO: Generating xml code left. Range expression requires a VType ? Should we replace it by a colon function call instead? 

		}
		caseExpr(node);
	}

	public void caseEndExpr(EndExpr node) {
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
		// TODO : Support Matrix expressions asap
		// if (node.getRows().getNumChild() > 1) {
		// System.out.println("Multiple rows now supported");
		// System.exit(1);
		// }
		// // for a single element
		// if (node.getRow(0).getElementList().getNumChild() == 1) {
		// node.getRow(0).getElement(0).analyze(this);
		//
		// } else {
		// // tuple type for multiple elements
		// HelperClass.toXML("TupleExpr");
		// HelperClass.toXML("elems");
		// for (Expr expr : node.getRow(0).getElementList()) {
		//
		// expr.analyze(this);
		//
		// }
		// HelperClass.toXML("/elems");
		// HelperClass.toXML("/TupleExpr");
		// }
		ExprCaseHandler.handleMatrixExpr(node, this);
		// caseLValueExpr(node);
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

	public void caseStringLiteralExpr(StringLiteralExpr node) {
		ExprCaseHandler.handleStringLiteralExpr(node, this);
		// caseLiteralExpr(node);
	}

	public static StringBuffer generateVrir(Function functionNode,
			Set<String> remainingVars,
			ValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis,
			ValueFlowMap<AggrValue<AdvancedMatrixValue>> currentOutSet,
			int index, int size, AnalysisEngine analysisEngine) {
		return (new VrirXmlGen(functionNode, remainingVars, analysis,
				currentOutSet, size, index, analysisEngine))
				.getPrettyPrintedCode();
	}

	public StringBuffer getPrettyPrintedCode() {
		return prettyPrintedCode;
	}

}
