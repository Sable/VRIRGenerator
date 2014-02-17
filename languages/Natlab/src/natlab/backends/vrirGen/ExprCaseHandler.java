package natlab.backends.vrirGen;

import natlab.tame.classes.reference.PrimitiveClassReference;
import ast.ColonExpr;
import ast.Expr;
import ast.FPLiteralExpr;
import ast.IntLiteralExpr;
import ast.MatrixExpr;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.RangeExpr;
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
				// ExprCaseHandler.handlePlusExpr(node, this);

			}
			// Function Call
			else {

				ExprCaseHandler.handleFunCallExpr(node, gen);

			}

		}
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
		}

		else {
			handleFunCallExpr(node, gen);
		}

		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleOpExpr(ParameterizedExpr node, VrirXmlGen gen,
			String name) {
		if (node.getArgList().getNumChild() == 2) {
			handleBinExpr(node, gen, name);
		} else if (node.getArgList().getNumChild() == 1) {
			handleUnaryExpr(node, gen, name);

		}
	}

	public static void handleMatrixExpr(MatrixExpr node, VrirXmlGen gen) {
		if (node.getRows().getNumChild() > 1) {
			System.out.println("Multiple rows now supported");
			System.exit(1);
		}
		// for a single element
		if (node.getRow(0).getElementList().getNumChild() == 1) {
			node.getRow(0).getElement(0).analyze(gen);

		} else {
			// tuple type for multiple elements

			// gen.appendToPrettyCode(toXMLHead("tuple", "1", "ndims"));
			gen.appendToPrettyCode(toXMLHead("tuple"));
			gen.appendToPrettyCode(HelperClass.toXML("elems"));
			for (Expr expr : node.getRow(0).getElementList()) {

				expr.analyze(gen);

			}
			gen.appendToPrettyCode(HelperClass.toXML("/elems"));
			gen.appendToPrettyCode(toXMLTail());
		}
	}

	public static void handleUnaryExpr(ParameterizedExpr node, VrirXmlGen gen,
			String name) {
		gen.appendToPrettyCode(toXMLHead(name));
		// TODO : Get unary expression type
		gen.appendToPrettyCode(HelperClass.getExprType(node, gen).toXML());
		gen.appendToPrettyCode(HelperClass.toXML("base"));
		node.getArg(0).analyze(gen);
		gen.appendToPrettyCode(HelperClass.toXML("/base"));
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleBinExpr(ParameterizedExpr node, VrirXmlGen gen,
			String name) {

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
		gen.appendToPrettyCode(toXMLHead(name));

		gen.appendToPrettyCode(HelperClass.getExprType(node, gen).toXML());
		gen.appendToPrettyCode("<rhs>\n");
		node.getArg(1).analyze(gen);
		gen.appendToPrettyCode("</rhs>\n");
		gen.appendToPrettyCode("<lhs>\n");
		node.getArg(0).analyze(gen);
		gen.appendToPrettyCode("</lhs>\n");
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleRangeExpr(RangeExpr expr, VrirXmlGen gen) {
		Expr start, step, stop;
		// gen.appendToPrettyCode(HelperClass.toXML("range"));
		// gen.appendToPrettyCode(HelperClass.toXML("start"));
		// expr.getLower().analyze(gen);
		// gen.appendToPrettyCode(HelperClass.toXML("/start"));
		//
		// if (expr.hasIncr()) {
		//
		// gen.appendToPrettyCode(HelperClass.toXML("step"));
		// expr.getIncr().analyze(gen);
		// gen.appendToPrettyCode(HelperClass.toXML("/step"));
		// }
		// gen.appendToPrettyCode(HelperClass.toXML("stop"));
		// expr.getUpper().analyze(gen);
		// gen.appendToPrettyCode(HelperClass.toXML("/stop"));
		// gen.appendToPrettyCode(HelperClass.toXML("/range"));
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
		gen.appendToPrettyCode(vt.toXML());
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
		} else {
			String field = "";
			if (vt instanceof VTypeMatrix) {
				if (((VTypeMatrix) vt).getType() == PrimitiveClassReference.DOUBLE) {
					field = "dval";
				} else if (((VTypeMatrix) vt).getType() == PrimitiveClassReference.SINGLE) {
					field = "fval";
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
		gen.appendToPrettyCode(vt.toXML());

		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleFunCallExpr(ParameterizedExpr expr, VrirXmlGen gen) {
		if (HelperClass.isAllocFunc(expr.getVarName())) {

			handleAllocExpr(expr, gen);
			return;

		}
		if (HelperClass.isLibFunc(expr.getVarName())) {
			handleLibCallExpr(expr, gen);
			return;
		}
		if (expr.getVarName().equals("colon")) {
			handleColonCall(expr, gen);
			return;
		}
		gen.appendToPrettyCode(toXMLHead("fncall", expr.getVarName(), "fnname"));
		VType vt = HelperClass.getExprType(expr, gen);
		if (vt == null) {
			throw new NullPointerException(
					"VType of function call expression could not be generated");
		}
		gen.appendToPrettyCode(vt.toXML());
		gen.appendToPrettyCode(HelperClass.toXML("args"));
		for (Expr args : expr.getArgList()) {
			args.analyze(gen);
		}
		gen.appendToPrettyCode(HelperClass.toXML("/args"));
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
		gen.appendToPrettyCode(HelperClass.toXML("args"));
		gen.appendToPrettyCode(HelperClass.toXML("/args"));
		gen.appendToPrettyCode(toXMLTail());

	}

	public static void handleColonExpr(ColonExpr node, VrirXmlGen gen) {
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
			VType vt = gen.getSymbol(arrayExpr.getVarName()).getVtype();
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

			gen.appendToPrettyCode(HelperClass.toXML("range"));

			gen.appendToPrettyCode(HelperClass.toXML("start"));

			gen.appendToPrettyCode(HelperClass.toXML("/start"));

			// if (node.getArgList().getNumChild() > 2) {
			// gen.appendToPrettyCode(HelperClass.toXML("step"));
			// node.getArg(indx).analyze(gen);
			// gen.appendToPrettyCode(HelperClass.toXML("/step"));
			// indx++;
			// }
			// gen.appendToPrettyCode(HelperClass.toXML("stop"));
			// node.getArg(indx).analyze(gen);
			// gen.appendToPrettyCode(HelperClass.toXML("/stop"));
			// indx++;
			// gen.appendToPrettyCode(HelperClass.toXML("/range"));
		}
	}

	public static void handleColonCall(ParameterizedExpr expr, VrirXmlGen gen) {
		// gen.appendToPrettyCode(HelperClass.toXML("range"));
		// int indx = 0;
		// gen.appendToPrettyCode(HelperClass.toXML("start"));
		// expr.getArg(indx).analyze(gen);
		// gen.appendToPrettyCode(HelperClass.toXML("/start"));
		// indx++;
		// if (expr.getArgList().getNumChild() > 2) {
		// gen.appendToPrettyCode(HelperClass.toXML("step"));
		// expr.getArg(indx).analyze(gen);
		// gen.appendToPrettyCode(HelperClass.toXML("/step"));
		// indx++;
		// }
		// gen.appendToPrettyCode(HelperClass.toXML("stop"));
		// expr.getArg(indx).analyze(gen);
		// gen.appendToPrettyCode(HelperClass.toXML("/stop"));
		// indx++;
		// gen.appendToPrettyCode(HelperClass.toXML("/range"));
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
		gen.appendToPrettyCode(toXMLHead("libcall", expr.getVarName(),
				"libfunc"));
		VType vt = HelperClass.getExprType(expr, gen);
		if (vt == null) {
			throw new NullPointerException(
					"VType of function call expression could not be generated");
		}
		gen.appendToPrettyCode(vt.toXML());
		gen.appendToPrettyCode(HelperClass.toXML("args"));
		for (Expr args : expr.getArgList()) {
			args.analyze(gen);
		}
		gen.appendToPrettyCode(HelperClass.toXML("/args"));
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
		gen.appendToPrettyCode(HelperClass.toXML("args"));
		for (Expr node : expr.getArgList()) {
			node.analyze(gen);
		}
		gen.appendToPrettyCode(HelperClass.toXML("/args"));
		gen.appendToPrettyCode(toXMLTail());
	}

	// TODO: Revisit . Problem with indices
	public static void handleIndexExpr(ParameterizedExpr expr, VrirXmlGen gen) {
		Symbol sym;

		if ((sym = gen.getSymbol(expr.getVarName())) == null) {
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
		gen.appendToPrettyCode(toXMLHead("index", "false", "flattened",
				"false", "copyslice",
				Integer.toString(gen.getSymbol(expr.getVarName()).getId()),
				"arrayid"));
		// gen.appendToPrettyCode(HelperClass.toXML("base"));
		// expr.getChild(0).analyze(gen);
		// gen.appendToPrettyCode(HelperClass.toXML("/base"));
		gen.appendToPrettyCode(sym.getVtype().toXML());
		gen.appendToPrettyCode(HelperClass.toXML("indices"));

		for (Expr args : expr.getArgList()) {
			gen.appendToPrettyCode(HelperClass
					.toXML("index boundscheck=\"1\" negative=\"0\""));

			args.analyze(gen);
			gen.appendToPrettyCode(HelperClass.toXML("/index"));
		}

		gen.appendToPrettyCode(HelperClass.toXML("/indices"));
		gen.appendToPrettyCode(toXMLTail());
	}

	public static void handleStringLiteralExpr(StringLiteralExpr expr,
			VrirXmlGen gen) {

		// gen.appendToPrettyCode(toXMLHead("const", expr.getValue(), "value"));
		// gen.appendToPrettyCode(HelperClass.getExprType(expr, gen).toXML());
		// gen.appendToPrettyCode(toXMLTail());
		throw new RuntimeException("VRIR Does Not Support Strings");
	}

	public static void handleRange(Expr start, Expr step, Expr stop,
			VrirXmlGen gen) {
		gen.appendToPrettyCode(HelperClass.toXML("range"));

		gen.appendToPrettyCode(HelperClass.toXML("start"));
		start.analyze(gen);
		gen.appendToPrettyCode(HelperClass.toXML("/start"));

		if (step != null) {
			gen.appendToPrettyCode(HelperClass.toXML("step"));
			step.analyze(gen);
			gen.appendToPrettyCode(HelperClass.toXML("/step"));
		}
		gen.appendToPrettyCode(HelperClass.toXML("stop"));
		stop.analyze(gen);
		gen.appendToPrettyCode(HelperClass.toXML("/stop"));

		gen.appendToPrettyCode(HelperClass.toXML("/range"));
	}

	public static StringBuffer toXMLHead(String name) {
		return new StringBuffer("<expr name=\"" + name + "\">\n");
	}

	public static StringBuffer toXMLHead(String name, int id, String field) {
		return new StringBuffer("<expr " + field + "=\"" + id + "\" name =\""
				+ name + "\">\n");
	}

	// public static StringBuffer toXMLHead(String name, String id, String
	// field) {
	// return new StringBuffer("<expr " + field + "=\"" + id + "\" name =\""
	// + name + "\">\n");
	// }
	public static StringBuffer toXMLHead(String name, String... fields) {
		if ((fields.length) % 2 != 0) {
			System.out.println("Number of arguments should be even ");
			return null;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("expr ");
		for (int i = 0; i < fields.length; i += 2) {
			sb.append(fields[i + 1] + "=\"" + fields[i] + "\" ");
		}
		sb.append("name =\"" + name + "\"");
		return new StringBuffer(HelperClass.toXML(sb.toString()));

	}

	public static StringBuffer toXMLTail() {
		return new StringBuffer("</expr>\n");
	}

}
