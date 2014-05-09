package natlab.backends.vrirGen;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import ast.ASTNode;
import ast.AssignStmt;
import ast.ColonExpr;
import ast.Expr;
import ast.List;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Program;
import ast.RangeExpr;
import ast.Stmt;
import natlab.CompilationProblem;
import natlab.Parse;
import natlab.toolkits.filehandling.GenericFile;
import nodecases.natlab.NatlabAbstractNodeCaseHandler;

public class ColonExprAnalysis extends NatlabAbstractNodeCaseHandler {
	private Stmt currStmt = null;
	private final String tempName = "dim_temp";
	private int tempNum = 1;
	HashSet<ColonExpr> colonExprSet = new HashSet<>();

	@Override
	public void caseASTNode(@SuppressWarnings("rawtypes") ASTNode node) {
		// TODO Auto-generated method stub
		for (int i = 0; i < node.getNumChild(); i++) {
			node.getChild(i).analyze(this);
		}
	}

	@Override
	public void caseColonExpr(ColonExpr node) {
		if (colonExprSet.contains(node)) {
			return;
		}
		if (!(node.getParent().getParent() instanceof ParameterizedExpr)) {
			throw new UnsupportedOperationException(
					"Parent of parent of ColonExpr is assumed to be a ParamaterizedExpr");
		}
		ParameterizedExpr arrayExpr = (ParameterizedExpr) node.getParent()
				.getParent();
		List<Expr> args = arrayExpr.getArgList();
		if (args.getIndexOfChild(node) < (args.getNumChild() - 1)) {
			int colonIndex = args.getIndexOfChild(node);
			ASTNode parentNode = currStmt.getParent();
			String colonTemp = tempName + tempNum++;
			NameExpr lhsExpr = new NameExpr(new Name(colonTemp));
			ast.List<Expr> argList = new ast.List<>();
			argList.add(new NameExpr(new Name(arrayExpr.getVarName())));
			argList.add(new NameExpr(new Name(Integer.toString(colonIndex + 1))));
			ParameterizedExpr rhsExpr = new ParameterizedExpr(
					(Expr) (new NameExpr(new Name("size"))), argList);
			AssignStmt stmt = new AssignStmt(lhsExpr, rhsExpr);
			parentNode.insertChild(stmt, parentNode.getIndexOfChild(currStmt));

		} else {
			System.out.println("flatten array");
		}
		colonExprSet.add(node);
		// caseExpr(node);
	}

	public void caseStmt(Stmt node) {
		currStmt = node;
		caseFunctionOrSignatureOrPropertyAccessOrStmt(node);
	}

	public static void analyze(Program program) {
		ColonExprAnalysis colonAnalysis = new ColonExprAnalysis();
		program.analyze(colonAnalysis);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileDir = "";// "capr/";
		String fileName = "simple.m";
		String fileIn = fileDir + fileName;
		File file = new File(fileIn);
		GenericFile gFile = GenericFile.create(file.getAbsolutePath());
		Program program = Parse.parseMatlabFile(gFile,
				new ArrayList<CompilationProblem>());
		ColonExprAnalysis.analyze(program);
		System.out.println(program.getPrettyPrinted());
	}

}
