package mini_python;

import java.util.HashMap;
import java.util.Iterator;

// the following exception is used whenever you have to implement something
class Todo extends Error {
  private static final long serialVersionUID = 1L;
  Todo() { super("TODO"); }
}

/* Values of Mini-Python.

   Two main differences wrt Python:

   - We use here machine integers (Java type `long`) while Python
     integers are arbitrary-precision integers (we could use Java
     big integers but we opt for simplicity here).

   - What Python calls a ``list'' is a resizeable array. In Mini-Python,
     there is no way to modify the length, so a mere Java array can be used.
*/

abstract class Value implements Comparable<Value> {
  abstract boolean isFalse();

  boolean isTrue() {
    throw new Todo(); // TODO (question 2)
  }

  long asInt() {
    if (!(this instanceof Vint))
      throw new Error("integer expected");
    return ((Vint) this).n;
  }

  Vlist asList() {
    if (!(this instanceof Vlist))
      throw new Error("list expected");
    return (Vlist) this;
  }
}

class Vnone extends Value {
  @Override
  boolean isFalse() {
    throw new Todo(); // TODO (question 2)
  }

  @Override
  public String toString() {
    return "None";
  }

  @Override
  public int compareTo(Value o) {
    return o instanceof Vnone ? 0 : -1;
  }
}

class Vbool extends Value {
  final boolean b;

  Vbool(boolean b) {
    this.b = b;
  }

  @Override
  public String toString() {
    return this.b ? "True" : "False";
  }

  @Override
  boolean isFalse() {
    throw new Todo(); // TODO (question 2)
  }

  @Override
  public int compareTo(Value o) {
    if (o instanceof Vnone)
      return 1;
    if (o instanceof Vbool) {
      boolean ob = ((Vbool) o).b;
      return b ? (ob ? 0 : 1) : (ob ? -1 : 0);
    }
    return -1;
  }
}

class Vint extends Value {
  final long n;

  Vint(long n) {
    this.n = n;
  }

  @Override
  public String toString() {
    return "" + this.n;
  }

  @Override
  boolean isFalse() {
    throw new Todo(); // TODO (question 2)
  }

  @Override
  public int compareTo(Value o) {
    if (o instanceof Vnone || o instanceof Vbool)
      return 1;
    if (o instanceof Vint) {
      long d = this.n - o.asInt();
      return d < 0 ? -1 : d > 0 ? 1 : 0;
    }
    return -1;
  }
}

class Vstring extends Value {
  final String s;

  Vstring(String s) {
    this.s = s;
  }

  @Override
  public String toString() {
    return this.s;
  }

  @Override
  boolean isFalse() {
    throw new Todo(); // TODO (question 2)
  }

  @Override
  public int compareTo(Value o) {
    if (o instanceof Vnone || o instanceof Vbool || o instanceof Vint)
      return 1;
    if (o instanceof Vstring)
      return this.s.compareTo(((Vstring) o).s);
    return -1;
  }
}

class Vlist extends Value {
  final Value[] l;

  Vlist(int n) {
    this.l = new Value[n];
  }

  Vlist(Value[] l1, Value[] l2) {
    this.l = new Value[l1.length + l2.length];
    System.arraycopy(l1, 0, l, 0, l1.length);
    System.arraycopy(l2, 0, l, l1.length, l2.length);
  }

  @Override
  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append("[");
    for (int i = 0; i < this.l.length; i++) {
      if (i != 0)
        b.append(", ");
      b.append(this.l[i]);
    }
    b.append("]");
    return b.toString();
  }

  @Override
  boolean isFalse() {
    throw new Todo(); // TODO (question 2)
  }

  @Override
  public int compareTo(Value o) {
    if (!(o instanceof Vlist))
      return -1;
    Value[] ol = ((Vlist) o).l;
    int n1 = this.l.length, n2 = ol.length;
    int i1 = 0, i2 = 0;
    for (; i1 < n1 && i2 < n2; i1++, i2++) {
      Value v1 = this.l[i1];
      Value v2 = ol[i2];
      int c = v1.compareTo(v2);
      if (c != 0)
        return c;
    }
    if (i1 < n1)
      return 1;
    if (i2 < n2)
      return -1;
    return 0;
  }
}

/* The following exception is used to interpret Python's `return`.

   Note: this is an unchecked exception, so that we don't have to
   add `throws` declarations to the visitor methods. */
class Return extends RuntimeException {
  private static final long serialVersionUID = 1L;

  final Value v;

  Return(Value v) {
    this.v = v;
  }
}

/* The interpreter starts here */

class Interp implements Visitor {

  /* The visitor methods do not return values (they have a `void` type).

     So, to return values when evaluating a constant or an expression,
     we use the following wrappers `evalConstant` and `evalExpr`.
     They call `accept` and the visitor assigns the variable `value`. */
  Value value = null;

  Value evalConstant(Constant c) {
    assert value == null; // check for non-reentrance
    c.accept(this);
    Value v = value;
    value = null;
    return v;
  }
  Value evalExpr(Expr e) {
    assert value == null; // check for non-reentrance
    e.accept(this);
    Value v = value;
    value = null;
    return v;
  }

  // interpreting constants is immediate
  public void visit(Cnone c) {
    this.value = new Vnone();
  }
  public void visit(Cbool c) {
    this.value = new Vbool(c.b);
  }
  public void visit(Cstring c) {
    this.value = new Vstring(c.s);
  }
  public void visit(Cint c) {
    this.value = new Vint(c.n);
  }

  // local variables
  HashMap<String, Value> vars;

  Interp() {
    this.vars = new HashMap<String, Value>();
  }

  // functions definitions (functions are global, hence `static`)
  static HashMap<String, Def> functions = new HashMap<String, Def>();

  // binary operators
  static Value binop(Binop op, Value v1, Value v2) {
    switch (op) {
    case Bsub:
    case Bmul:
    case Bdiv:
    case Bmod:
      // TODO (question 1) 
      long i1 = v1.asInt();
      long i2 = v2.asInt();
      switch (op) {
      case Bsub:
        return new Vint(i1 - i2);
      case Bmul:
        return new Vint(i1 * i2);
      case Bdiv:
        if (i2 == 0)
          throw new Error("division by zero");
        return new Vint(i1 / i2);
      case Bmod:
        if (i2 == 0)
          throw new Error("division by zero");
        return new Vint(i1 % i2);
      default:
    }
    break;
    case Badd:// TODO (question 1) 
      if (v1 instanceof Vint && v2 instanceof Vint)
        return new Vint(v1.asInt() + v2.asInt());
      if (v1 instanceof Vstring && v2 instanceof Vstring)
        throw new Todo(); // TODO (question 3)
      if (v1 instanceof Vlist && v2 instanceof Vlist)
        throw new Todo(); // TODO (question 5)
      break;
    case Beq:
      throw new Todo(); // TODO (question 2)
    case Bneq:
      throw new Todo(); // TODO (question 2)
    case Blt:
      throw new Todo(); // TODO (question 2)
    case Ble:
      throw new Todo(); // TODO (question 2)
    case Bgt:
      throw new Todo(); // TODO (question 2)
    case Bge:
      throw new Todo(); // TODO (question 2)
    default:
    }
    throw new Error("unsupported operand types");
  }

  // interpreting expressions

  @Override
  public void visit(Ecst e) {
    this.value = evalConstant(e.c);
  }

  @Override
  public void visit(Ebinop e) {
    Value v1 = evalExpr(e.e1);
    switch (e.op) {
    case Band:
      throw new Todo(); // TODO (question 2)
    case Bor:
      throw new Todo(); // TODO (question 2)
    default:
      this.value = binop(e.op, v1, evalExpr(e.e2));
    }
  }

  @Override
  public void visit(Eunop e) {
    switch (e.op) {
    case Unot:
      throw new Todo(); // TODO (question 2)
    case Uneg:// TODO (question 1)
    this.value = new Vint(-evalExpr(e.e).asInt());
    break; 
    }
  }

  @Override
  public void visit(Eident id) {
    throw new Todo(); // TODO (question 3)
  }

  @Override
  public void visit(Ecall e) {
    switch (e.f.id) {
    case "len":
      throw new Todo(); // TODO (question 5)
    case "list":
      throw new Todo(); // TODO (question 5)
    case "range":
      throw new Todo(); // TODO (question 5)
    default:
      throw new Todo(); // TODO (question 4)
    }
  }

  @Override
  public void visit(Elist e) {
    throw new Todo(); // TODO (question 5)
  }

  @Override
  public void visit(Eget e) {
    throw new Todo(); // TODO (question 5)
  }

  // interpreting statements

  @Override
  public void visit(Seval s) {
    s.e.accept(this);
  }

  @Override
  public void visit(Sprint s) {
    System.out.println(evalExpr(s.e).toString());
  }

  @Override
  public void visit(Sblock s) {
    for (Stmt st: s.l)
      st.accept(this);
  }

  @Override
  public void visit(Sif s) {
    throw new Todo(); // TODO (question 2)
  }

  @Override
  public void visit(Sassign s) {
    throw new Todo(); // TODO (question 3)
  }

  @Override
  public void visit(Sreturn s) {
    throw new Todo(); // TODO (question 4)
  }

  @Override
  public void visit(Sfor s) {
    throw new Todo(); // TODO (question 5)
  }

  @Override
  public void visit(Sset s) {
    throw new Todo(); // TODO (question 5)
  }
}
