package natlab.backends.vrirGen;

import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.components.shape.DimValue;
import ast.ColonExpr;
import ast.Expr;
import ast.FPLiteralExpr;
import ast.IntLiteralExpr;
import ast.MatrixExpr;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.RangeExpr;
import ast.ShortCircuitAndExpr;
import ast.ShortCircuitOrExpr;
import ast.StringLiteralExpr;

public class ExprCaseHandler {
	public static void handleParameterizedExpr(ParameterizedExpr node,
			VrirXmlGen gen) {
		if (gen.getRemainingVars().contains(node.getVarName())) {
			ExprCaseHandler.handleIndexExpr(node, gen);
		} else {
			// Operator
			if (OperatorMapper.isOperator(node.getVarName())) {
				// Binary operator
				ExprCaseHandler.handleOpExpr(node, gen,
						OperatorMapper.get(node.getVarName()));
			}
			// Function Call
			else {
				ExprCaseHandler.handleFunCallExpr(node, gen);
			}
		}
	}

	public static void handleShortCircuitAndExpr(ShortCircuitAndExpr node,
			VrirXmlGen gen) {

		gen.appendToPrettyCode(ExprCaseHandler.toXMLHead("and"));
		VType vt = HelperClass.getExprType(node, gen);
		gen.appendToPrettyCode(vt.toXML());
		gen.appendToPrettyCode(HelperClass.toXMLHead("lhs"));
		node.getLHS().analyze(gen);
		gen.appendToPrettyCode(HelperClass.toXMLTail());
		gen.appendToPrettyCode(HelperClass.toXMLHead("rhs"));
		node.getRHS().analyze(gen);
		gen.appendToPrettyCode(HelperClass.toXMLTail());
		gen.appendToPrettyCode(ExprCaseHandler.toXMLTail());
	}

	public static void handleShortCircuitOrExpr(ShortCircuitOrExpr node,
			VrirXmlGen gen) {
		gen.appendToPrettyCode(ExprCaseHandler.toXMLHead("or"));
		VType vt = HelperClass.getExprType(node, gen);
		gen.appendToPrettyCode(vt.toXML());
		node.getLHS().analyze(gen);
		node.getRHS().analyze(gen);
		gen.appendToPrettyCode(ExprCaseHandler.toXMLTail());
	}

	public static void handleNameExpr(NameExpr node, VrirXmlGen gen) {
		if (HelperClass.isVar(gen, node.getName().getID())) {

			if (!gen.getSymTab().contains(node.getName().getID())) {

				VType vtype = HelperClass.generateVType(gen.getAnalysis(),
						gen.getIndex(), node.getName().getID());
				gen.getSymTab().putSymbol(vtype, node.getName().getID());
			}
			if (gen.getSymbol(node.getName().getID()) != null) {
				gen.appendToPrettyCode(toXMLHead("name",
						gen.getSymbol(node.getName().getID()).getId(), "id"));

			} else {
				throw new NullPointerException("Symbol not found for "
						+ node.getName().getID());
			}
			gen.appendToPrettyCode(gen.getSymbol(node.getName().getID())
					.getVtype().toXML());
			gen.appendToPrettyCode(toXMLTail());
		}

		else {
			handleFunCallExpr(node, gen);
		}

	}

	public static void handleOpExpr(ParameterizedExpr node, VrirXmlGen gen,
			String name) {
		boolean flag = false;
		if (name.trim().equalsIgnoreCase("mmult")) {
			VType vt = HelperClass.getExprType(node.getArg(0), gen);
			if (vt instanceof VTypeMatrix) {

				if (((VTypeMatrix) vt).getShape().getDimensions().get(0)
						.equalsOne()
						&& ((VTypeMatrix) vt).getShape().getDimensions().get(1)
								.equalsOne()
						&& (((VTypeMatrix) vt).getShape().getDimensions()
								.size() == 2)) {
					name = "mult";
				}
			} else {
				throw new UnsupportedOperationException(
						"operations on cell arrays not supported");
			}
			vt = HelperClass.getExprType(node.getArg(1), gen);
			if (vt instanceof VTypeMatrix) {

				if (((VTypeMatrix) vt).getShape().getDimensions().get(0)
						.equalsOne()
						&& ((VTypeMatrix) vt).getShape().getDimensions().get(1)
								.equalsOne()
						&& (((VTypeMatrix) vt).getShape().getDimensions()
								.size() == 2)) {
					name = "mult";
				}
			} else {
				throw new UnsupportedOperationException(
						"operations on cell arrays not supported");
			}

		}
		if (name.trim().equalsIgnoreCase("mrdiv")
				|| name.trim().equalsIgnoreCase("mldiv")) {
			VType vt = HelperClass.getExprType(node.getArg(0), gen);
			if (vt instanceof VTypeMatrix) {

				if (((VTypeMatrix) vt).getShape().getDimensions().get(0)
						.equalsOne()
						&& ((VTypeMatrix) vt).getShape().getDimensions().get(1)
								.equalsOne()
						&& (((VTypeMatrix) vt).getShape().getDimensions()
								.size() == 2)) {
					name = "div";
				}
			} else {
				throw new UnsupportedOperationException(
						"operations on cell arrays not supported");
			}
			vt = HelperClass.getExprType(node.getArg(1), gen);
			if (vt instanceof VTypeMatrix) {

				if (((VTypeMatrix) vt).getShape().getDimensions().get(0)
						.equalsOne()
						&& ((VTypeMatrix) vt).getShape().getDimensions().get(1)
								.equalsOne()
						&& (((VTypeMatrix) vt).getShape().getDimensions()
								.size() == 2)) {
					name = "div";
				}
			} else {
				throw new UnsupportedOperationException(
						"operations on cell arrays not supported");
			}
		}
		for (Expr expr : node.getArgList()) {
			VType vt = HelperClass.getExprType(expr, gen);
			if (vt instanceof VTypeMatrix) {
				DimValue dim1 = ((VTypeMatrix) vt).getShape().getDimensions()
						.get(0);
				DimValue dim2 = ((VTypeMatrix) vt).getShape().getDimensions()
						.get(1);

				if (!dim1.equalsOne() || !dim2.equalsOne()) {
					flag = true;
					break;
				}
			}
		}
		if (flag && LibFuncMapper.containsFunc(node.getVarName())) {
			handleLibCallExpr(node, gen);
		} else {
			if (node.getArgList().getNumChild() == 2) {
				handleBinExpr(node, gen, name);
			} else if (node.getArgList().getNumChild() == 1) {
				handleUnaryExpr(node, gen, name);

			}
		}
	}

	public static void handleMatrixExpr(MatrixExpr node, VrirXmlGen gen) {
		if (node.getRows().getNumChild() > 1) {
			throw new UnsupportedOperationException(
					"Multiple rows not supported");
		}
		// for a single element
		if (node.getRow(0).getElementList().getNumChild() == 1) {
			node.getRow(0).getElement(0).analyze(gen);

		} else {
			VType vt = HelperClass.getExprType(node, gen);
			if (vt == null) {
				throw new NullPointerException(
						"vtype for matrix expression is null");
			}
			gen.appendToPrettyCode(toXMLHead("tuple"));
			gen.appendToPrettyCode(vt.toXML());
			gen.appendToPrettyCode(HelperClass.toXMLHead("elems"));
			for (Expr expr : node.getRow(0).getElementList()) {

				expr.analyze(gen);

			}
			gen.appendToPrettyCode(HelperClass.toXMLTail());
			gen.appendToPrettyCode(toXMLTail());
		}
	}

	public static void handleUnaryExpr(ParameterizedExpr node, VrirXmlGen gen,
			String name) {
		gen.appendToPrettyCode(toXMLHead(name));
		gen.appendToPrettyCode(HelperClass.getExprType(node, gen).toXML());
		node.getArg(0).analyze(gen);
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleBinExpr(ParameterizedExpr node, VrirXmlGen gen,
			String name) {
		gen.appendToPrettyCode(toXMLHead(name));
		gen.appendToPrettyCode(HelperClass.getExprType(node, gen).toXML());
		gen.appendToPrettyCode(HelperClass.toXMLHead("lhs"));
		node.getArg(0).analyze(gen);
		gen.appendToPrettyCode(HelperClass.toXMLTail());
		gen.appendToPrettyCode(HelperClass.toXMLHead("rhs"));
		node.getArg(1).analyze(gen);
		gen.appendToPrettyCode(HelperClass.toXMLTail());
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleRangeExpr(RangeExpr expr, VrirXmlGen gen) {
		Expr start, step, stop;
		start = expr.getLower();
		step = expr.hasIncr() ? expr.getIncr() : null;
		stop = expr.getUpper();
		handleRange(start, step, stop, gen);
	}

	public static void handleIntLiteralExpr(IntLiteralExpr expr, VrirXmlGen gen) {
		VType vt = HelperClass.getExprType(expr, gen);
		if (vt == null) {
			throw new NullPointerException(
					"Could not generate vtype for const expression");
		}
		if (expr.getValue().isImaginary()) {
			// TODO : Handle complex integers
			throw new RuntimeException(
					"Complex constants are not currently supported");
		} else {
			String field = "";
			if (vt instanceof VTypeMatrix) {
				if (((VTypeMatrix) vt).getType() == PrimitiveClassReference.DOUBLE) {
					field = "dval";
				} else if (((VTypeMatrix) vt).getType() == PrimitiveClassReference.SINGLE) {
					field = "fval";
				} else if (((VTypeMatrix) vt).getType() == PrimitiveClassReference.INT32
						|| ((VTypeMatrix) vt).getType() == PrimitiveClassReference.INT64
						|| ((VTypeMatrix) vt).getType() == PrimitiveClassReference.INT16
						|| ((VTypeMatrix) vt).getType() == PrimitiveClassReference.INT8) {
					field = "ival";

				} else {
					throw new UnsupportedOperationException(
							"cant identify type" + ((VTypeMatrix) vt).getType());
				}
			} else {
				throw new UnsupportedOperationException("Cannot identify VType"
						+ vt.getClass());
			}
			gen.appendToPrettyCode(toXMLHead("realconst", expr.getValue()
					.getValue().toString(), field));
		}
		if (vt instanceof VTypeMatrix) {
			gen.appendToPrettyCode(((VTypeMatrix) vt).toXML(true));
		} else {
			gen.appendToPrettyCode(vt.toXML());
		}
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleFpLiteralExpr(FPLiteralExpr expr, VrirXmlGen gen) {
		VType vt = HelperClass.getExprType(expr, gen);
		if (vt == null) {
			throw new NullPointerException(
					"Could not generate vtype for const expression");
		}
		if (expr.getValue().isImaginary()) {
			// TODO: Handle complex floats
			throw new RuntimeException(
					"Complex constants are not currently supported");
		} else {
			String field = "";
			if (vt instanceof VTypeMatrix) {
				if (((VTypeMatrix) vt).getType() == PrimitiveClassReference.DOUBLE) {
					field = "dval";
				} else if (((VTypeMatrix) vt).getType() == PrimitiveClassReference.SINGLE) {
					field = "fval";
				} else if (((VTypeMatrix) vt).getType() == PrimitiveClassReference.INT64) {
					field = "ival";
				} else {
					throw new UnsupportedOperationException(
							"cant identify type" + ((VTypeMatrix) vt).getType());
				}
			} else {
				throw new UnsupportedOperationException("Cannot identify VType"
						+ vt.getClass());
			}
			gen.appendToPrettyCode(toXMLHead("realconst", expr.getValue()
					.getValue().toString(), field));
		}
		if (vt instanceof VTypeMatrix) {
			gen.appendToPrettyCode(((VTypeMatrix) vt).toXML(true));
		} else {
			gen.appendToPrettyCode(vt.toXML());
		}
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleFunCallExpr(ParameterizedExpr expr, VrirXmlGen gen) {
		if (HelperClass.isAllocFunc(expr.getVarName())) {

			handleAllocExpr(expr, gen);
			return;

		}

		if (LibFuncMapper.containsFunc(expr.getVarName())) {

			handleLibCallExpr(expr, gen);
			return;
		}
		gen.appendToPrettyCode(toXMLHead("fncall", expr.getVarName(), "fnname"));
		VType vt = HelperClass.getExprType(expr, gen);
		if (vt == null) {
			throw new NullPointerException(
					"VType of function call expression could not be generated");
		}
		gen.appendToPrettyCode(vt.toXML());
		gen.appendToPrettyCode(HelperClass.toXMLHead("args"));
		for (Expr args : expr.getArgList()) {
			args.analyze(gen);
		}
		gen.appendToPrettyCode(HelperClass.toXMLTail());
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleFunCallExpr(NameExpr expr, VrirXmlGen gen) {
		gen.appendToPrettyCode(toXMLHead("fncall", expr.getName().getID(),
				"fnname"));

		VType vt = HelperClass.getExprType(expr, gen);
		if (vt == null) {
			throw new NullPointerException(
					"VType of function call expression could not be generated");
		}
		gen.appendToPrettyCode(vt.toXML());
		gen.appendToPrettyCode(HelperClass.toXMLHead("args"));
		gen.appendToPrettyCode(HelperClass.toXMLTail());
		gen.appendToPrettyCode(toXMLTail());

	}

	public static void handleColonExpr(ColonExpr node, VrirXmlGen gen) {
		throw new UnsupportedOperationException(
				"Colon Expression should have been converted into a range expression before hand");
	}

	public static void handleColonCall(ParameterizedExpr expr, VrirXmlGen gen) {
		Expr start, step, stop;
		int indx = 0;
		start = expr.getArg(indx);
		if (expr.getArgList().getNumChild() > 2) {
			indx++;
			step = expr.getArg(indx);
		} else {
			step = null;
		}
		++indx;
		stop = expr.getArg(indx);
		handleRange(start, step, stop, gen);
	}

	public static void handleLibCallExpr(ParameterizedExpr expr, VrirXmlGen gen) {
		String name = LibFuncMapper.getFunc(expr.getVarName());
		if (name.trim().equalsIgnoreCase("mmult")) {
			VType vt = HelperClass.getExprType(expr.getArg(0), gen);
			if (vt instanceof VTypeMatrix) {

				if (((VTypeMatrix) vt).getShape().getDimensions().get(0)
						.equalsOne()
						&& ((VTypeMatrix) vt).getShape().getDimensions().get(1)
								.equalsOne()
						&& (((VTypeMatrix) vt).getShape().getDimensions()
								.size() == 2)) {
					name = "libmult";
				}
			} else {
				throw new UnsupportedOperationException(
						"operations on cell arrays not supported");
			}
			vt = HelperClass.getExprType(expr.getArg(1), gen);
			if (vt instanceof VTypeMatrix) {

				if (((VTypeMatrix) vt).getShape().getDimensions().get(0)
						.equalsOne()
						&& ((VTypeMatrix) vt).getShape().getDimensions().get(1)
								.equalsOne()
						&& (((VTypeMatrix) vt).getShape().getDimensions()
								.size() == 2)) {
					name = "libmult";
				}
			} else {
				throw new UnsupportedOperationException(
						"operations on cell arrays not supported");
			}

		}
		gen.appendToPrettyCode(toXMLHead("libcall", name, "libfunc"));
		if (LibFuncMapper.getFunc(expr.getVarName()) == null) {
			throw new NullPointerException("lib call could not be found "
					+ expr.getVarName());
		}
		VType vt = HelperClass.getExprType(expr, gen);
		if (vt == null) {
			throw new NullPointerException(
					"VType of function call expression could not be generated");
		}
		gen.appendToPrettyCode(vt.toXML());
		gen.appendToPrettyCode(HelperClass.toXMLHead("args"));
		for (Expr args : expr.getArgList()) {
			args.analyze(gen);
		}
		gen.appendToPrettyCode(HelperClass.toXMLTail());
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleAllocExpr(ParameterizedExpr expr, VrirXmlGen gen) {
		gen.appendToPrettyCode(toXMLHead("alloc", expr.getVarName(), "func"));
		VType vt = HelperClass.getExprType(expr, gen);
		if (vt == null) {
			throw new NullPointerException(
					"VType of function call expression could not be generated");
		}
		gen.appendToPrettyCode(vt.toXML());
		gen.appendToPrettyCode(HelperClass.toXMLHead("args"));
		for (Expr node : expr.getArgList()) {
			node.analyze(gen);
		}
		gen.appendToPrettyCode(HelperClass.toXMLTail());
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleIndexExpr(ParameterizedExpr expr, VrirXmlGen gen) {
		Symbol sym = gen.getSymbol(expr.getVarName());
		if (sym == null) {
			VType vtype = HelperClass.getExprType(expr, gen);
			if (vtype == null) {
				throw new NullPointerException(
						"No Vtype found for paratemerized expression "
								+ expr.getVarName());
			}

			gen.addToSymTab(vtype, expr.getVarName());
			sym = gen.getSymbol(expr.getVarName());
		}
		if (sym == null) {
			throw new NullPointerException("Symbol not found in symbol table ");
		}
		gen.appendToPrettyCode(toXMLHead("index",
				Integer.toString(sym.getId()), "arrayid", "%0", "copyslice"));
		VType vt = HelperClass.getExprType(expr, gen);
		gen.appendToPrettyCode(vt.toXML());
		gen.appendToPrettyCode(HelperClass.toXMLHead("indices"));
		for (Expr arg : expr.getArgList()) {
			gen.appendToPrettyCode(HelperClass
					.toXMLHead("index :boundscheck %1 :negative %0"));
			if (arg instanceof ParameterizedExpr
					&& arg.getVarName().equals("colon")) {
				handleColonCall((ParameterizedExpr) arg, gen);
				gen.appendToPrettyCode(HelperClass.toXMLTail());
				continue;
			}
			arg.analyze(gen);
			gen.appendToPrettyCode(HelperClass.toXMLTail());

		}
		gen.appendToPrettyCode(HelperClass.toXMLTail());
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleStringLiteralExpr(StringLiteralExpr expr,
			VrirXmlGen gen) {
		//throw new RuntimeException("VRIR Does Not Support Strings");
	}

	public static void handleRange(Expr start, Expr step, Expr stop,
			VrirXmlGen gen) {
		gen.appendToPrettyCode(HelperClass.toXMLHead("range :exclude %0"));

		gen.appendToPrettyCode(HelperClass.toXMLHead("start "));
		start.analyze(gen);
		gen.appendToPrettyCode(HelperClass.toXMLTail());

		if (step != null) {
			gen.appendToPrettyCode(HelperClass.toXMLHead("step "));
			step.analyze(gen);
			gen.appendToPrettyCode(HelperClass.toXMLTail());
		}
		gen.appendToPrettyCode(HelperClass.toXMLHead("stop "));
		stop.analyze(gen);
		gen.appendToPrettyCode(HelperClass.toXMLTail());
		gen.appendToPrettyCode(HelperClass.toXMLTail());
	}

	public static StringBuffer toXMLHead(String name) {
		return new StringBuffer("(" + name + "\n");
	}

	public static StringBuffer toXMLHead(String name, int id, String field) {
		return new StringBuffer("(" + name + " :" + field + " " + id + "\n");
	}

	public static StringBuffer toXMLHead(String name, String... fields) {
		if ((fields.length) % 2 != 0) {
			System.err.println("Number of arguments should be even ");
			return null;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("(" + name);
		for (int i = 0; i < fields.length; i += 2) {
			sb.append(" :" + fields[i + 1] + " " + fields[i]);
		}
		return sb;

	}

	public static StringBuffer toXMLTail() {
		return new StringBuffer(")");
	}

}
