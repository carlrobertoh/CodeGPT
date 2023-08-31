// Generated from PythonParser.g4 by ANTLR 4.13.0
package grammar;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class PythonParser extends PythonParserBase {
	static { RuntimeMetaData.checkVersion("4.13.0", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		INDENT=1, DEDENT=2, LINE_BREAK=3, DEF=4, RETURN=5, RAISE=6, FROM=7, IMPORT=8, 
		NONLOCAL=9, AS=10, GLOBAL=11, ASSERT=12, IF=13, ELIF=14, ELSE=15, WHILE=16, 
		FOR=17, IN=18, TRY=19, NONE=20, FINALLY=21, WITH=22, EXCEPT=23, LAMBDA=24, 
		OR=25, AND=26, NOT=27, IS=28, CLASS=29, YIELD=30, DEL=31, PASS=32, CONTINUE=33, 
		BREAK=34, ASYNC=35, AWAIT=36, PRINT=37, EXEC=38, TRUE=39, FALSE=40, DOT=41, 
		ELLIPSIS=42, REVERSE_QUOTE=43, STAR=44, COMMA=45, COLON=46, SEMI_COLON=47, 
		POWER=48, ASSIGN=49, OR_OP=50, XOR=51, AND_OP=52, LEFT_SHIFT=53, RIGHT_SHIFT=54, 
		ADD=55, MINUS=56, DIV=57, MOD=58, IDIV=59, NOT_OP=60, LESS_THAN=61, GREATER_THAN=62, 
		EQUALS=63, GT_EQ=64, LT_EQ=65, NOT_EQ_1=66, NOT_EQ_2=67, AT=68, ARROW=69, 
		ADD_ASSIGN=70, SUB_ASSIGN=71, MULT_ASSIGN=72, AT_ASSIGN=73, DIV_ASSIGN=74, 
		MOD_ASSIGN=75, AND_ASSIGN=76, OR_ASSIGN=77, XOR_ASSIGN=78, LEFT_SHIFT_ASSIGN=79, 
		RIGHT_SHIFT_ASSIGN=80, POWER_ASSIGN=81, IDIV_ASSIGN=82, STRING=83, DECIMAL_INTEGER=84, 
		OCT_INTEGER=85, HEX_INTEGER=86, BIN_INTEGER=87, IMAG_NUMBER=88, FLOAT_NUMBER=89, 
		OPEN_PAREN=90, CLOSE_PAREN=91, OPEN_BRACE=92, CLOSE_BRACE=93, OPEN_BRACKET=94, 
		CLOSE_BRACKET=95, NAME=96, LINE_JOIN=97, NEWLINE=98, WS=99, COMMENT=100;
	public static final int
		RULE_root = 0, RULE_single_input = 1, RULE_file_input = 2, RULE_eval_input = 3, 
		RULE_stmt = 4, RULE_compound_stmt = 5, RULE_suite = 6, RULE_decorator = 7, 
		RULE_elif_clause = 8, RULE_else_clause = 9, RULE_finally_clause = 10, 
		RULE_with_item = 11, RULE_except_clause = 12, RULE_classdef = 13, RULE_funcdef = 14, 
		RULE_typedargslist = 15, RULE_args = 16, RULE_kwargs = 17, RULE_def_parameters = 18, 
		RULE_def_parameter = 19, RULE_named_parameter = 20, RULE_simple_stmt = 21, 
		RULE_small_stmt = 22, RULE_testlist_star_expr = 23, RULE_star_expr = 24, 
		RULE_assign_part = 25, RULE_exprlist = 26, RULE_import_as_names = 27, 
		RULE_import_as_name = 28, RULE_dotted_as_names = 29, RULE_dotted_as_name = 30, 
		RULE_test = 31, RULE_varargslist = 32, RULE_vardef_parameters = 33, RULE_vardef_parameter = 34, 
		RULE_varargs = 35, RULE_varkwargs = 36, RULE_logical_test = 37, RULE_comparison = 38, 
		RULE_expr = 39, RULE_atom = 40, RULE_dictorsetmaker = 41, RULE_testlist_comp = 42, 
		RULE_testlist = 43, RULE_dotted_name = 44, RULE_name = 45, RULE_number = 46, 
		RULE_integer = 47, RULE_yield_expr = 48, RULE_yield_arg = 49, RULE_trailer = 50, 
		RULE_arguments = 51, RULE_arglist = 52, RULE_argument = 53, RULE_subscriptlist = 54, 
		RULE_subscript = 55, RULE_sliceop = 56, RULE_comp_for = 57, RULE_comp_iter = 58;
	private static String[] makeRuleNames() {
		return new String[] {
			"root", "single_input", "file_input", "eval_input", "stmt", "compound_stmt", 
			"suite", "decorator", "elif_clause", "else_clause", "finally_clause", 
			"with_item", "except_clause", "classdef", "funcdef", "typedargslist", 
			"args", "kwargs", "def_parameters", "def_parameter", "named_parameter", 
			"simple_stmt", "small_stmt", "testlist_star_expr", "star_expr", "assign_part", 
			"exprlist", "import_as_names", "import_as_name", "dotted_as_names", "dotted_as_name", 
			"test", "varargslist", "vardef_parameters", "vardef_parameter", "varargs", 
			"varkwargs", "logical_test", "comparison", "expr", "atom", "dictorsetmaker", 
			"testlist_comp", "testlist", "dotted_name", "name", "number", "integer", 
			"yield_expr", "yield_arg", "trailer", "arguments", "arglist", "argument", 
			"subscriptlist", "subscript", "sliceop", "comp_for", "comp_iter"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, "'def'", "'return'", "'raise'", "'from'", "'import'", 
			"'nonlocal'", "'as'", "'global'", "'assert'", "'if'", "'elif'", "'else'", 
			"'while'", "'for'", "'in'", "'try'", "'None'", "'finally'", "'with'", 
			"'except'", "'lambda'", "'or'", "'and'", "'not'", "'is'", "'class'", 
			"'yield'", "'del'", "'pass'", "'continue'", "'break'", "'async'", "'await'", 
			"'print'", "'exec'", "'True'", "'False'", "'.'", "'...'", "'`'", "'*'", 
			"','", "':'", "';'", "'**'", "'='", "'|'", "'^'", "'&'", "'<<'", "'>>'", 
			"'+'", "'-'", "'/'", "'%'", "'//'", "'~'", "'<'", "'>'", "'=='", "'>='", 
			"'<='", "'<>'", "'!='", "'@'", "'->'", "'+='", "'-='", "'*='", "'@='", 
			"'/='", "'%='", "'&='", "'|='", "'^='", "'<<='", "'>>='", "'**='", "'//='", 
			null, null, null, null, null, null, null, "'('", "')'", "'{'", "'}'", 
			"'['", "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INDENT", "DEDENT", "LINE_BREAK", "DEF", "RETURN", "RAISE", "FROM", 
			"IMPORT", "NONLOCAL", "AS", "GLOBAL", "ASSERT", "IF", "ELIF", "ELSE", 
			"WHILE", "FOR", "IN", "TRY", "NONE", "FINALLY", "WITH", "EXCEPT", "LAMBDA", 
			"OR", "AND", "NOT", "IS", "CLASS", "YIELD", "DEL", "PASS", "CONTINUE", 
			"BREAK", "ASYNC", "AWAIT", "PRINT", "EXEC", "TRUE", "FALSE", "DOT", "ELLIPSIS", 
			"REVERSE_QUOTE", "STAR", "COMMA", "COLON", "SEMI_COLON", "POWER", "ASSIGN", 
			"OR_OP", "XOR", "AND_OP", "LEFT_SHIFT", "RIGHT_SHIFT", "ADD", "MINUS", 
			"DIV", "MOD", "IDIV", "NOT_OP", "LESS_THAN", "GREATER_THAN", "EQUALS", 
			"GT_EQ", "LT_EQ", "NOT_EQ_1", "NOT_EQ_2", "AT", "ARROW", "ADD_ASSIGN", 
			"SUB_ASSIGN", "MULT_ASSIGN", "AT_ASSIGN", "DIV_ASSIGN", "MOD_ASSIGN", 
			"AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", "LEFT_SHIFT_ASSIGN", "RIGHT_SHIFT_ASSIGN", 
			"POWER_ASSIGN", "IDIV_ASSIGN", "STRING", "DECIMAL_INTEGER", "OCT_INTEGER", 
			"HEX_INTEGER", "BIN_INTEGER", "IMAG_NUMBER", "FLOAT_NUMBER", "OPEN_PAREN", 
			"CLOSE_PAREN", "OPEN_BRACE", "CLOSE_BRACE", "OPEN_BRACKET", "CLOSE_BRACKET", 
			"NAME", "LINE_JOIN", "NEWLINE", "WS", "COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "PythonParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PythonParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RootContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(PythonParser.EOF, 0); }
		public Single_inputContext single_input() {
			return getRuleContext(Single_inputContext.class,0);
		}
		public File_inputContext file_input() {
			return getRuleContext(File_inputContext.class,0);
		}
		public Eval_inputContext eval_input() {
			return getRuleContext(Eval_inputContext.class,0);
		}
		public RootContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_root; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterRoot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitRoot(this);
		}
	}

	public final RootContext root() throws RecognitionException {
		RootContext _localctx = new RootContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_root);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(121);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(118);
				single_input();
				}
				break;
			case 2:
				{
				setState(119);
				file_input();
				}
				break;
			case 3:
				{
				setState(120);
				eval_input();
				}
				break;
			}
			setState(123);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Single_inputContext extends ParserRuleContext {
		public TerminalNode LINE_BREAK() { return getToken(PythonParser.LINE_BREAK, 0); }
		public Simple_stmtContext simple_stmt() {
			return getRuleContext(Simple_stmtContext.class,0);
		}
		public Compound_stmtContext compound_stmt() {
			return getRuleContext(Compound_stmtContext.class,0);
		}
		public Single_inputContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_single_input; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterSingle_input(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitSingle_input(this);
		}
	}

	public final Single_inputContext single_input() throws RecognitionException {
		Single_inputContext _localctx = new Single_inputContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_single_input);
		try {
			setState(130);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(125);
				match(LINE_BREAK);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(126);
				simple_stmt();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(127);
				compound_stmt();
				setState(128);
				match(LINE_BREAK);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class File_inputContext extends ParserRuleContext {
		public List<TerminalNode> LINE_BREAK() { return getTokens(PythonParser.LINE_BREAK); }
		public TerminalNode LINE_BREAK(int i) {
			return getToken(PythonParser.LINE_BREAK, i);
		}
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public File_inputContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file_input; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterFile_input(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitFile_input(this);
		}
	}

	public final File_inputContext file_input() throws RecognitionException {
		File_inputContext _localctx = new File_inputContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_file_input);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(134); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					setState(134);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
					case 1:
						{
						setState(132);
						match(LINE_BREAK);
						}
						break;
					case 2:
						{
						setState(133);
						stmt();
						}
						break;
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(136); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Eval_inputContext extends ParserRuleContext {
		public TestlistContext testlist() {
			return getRuleContext(TestlistContext.class,0);
		}
		public List<TerminalNode> LINE_BREAK() { return getTokens(PythonParser.LINE_BREAK); }
		public TerminalNode LINE_BREAK(int i) {
			return getToken(PythonParser.LINE_BREAK, i);
		}
		public Eval_inputContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eval_input; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterEval_input(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitEval_input(this);
		}
	}

	public final Eval_inputContext eval_input() throws RecognitionException {
		Eval_inputContext _localctx = new Eval_inputContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_eval_input);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(138);
			testlist();
			setState(142);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LINE_BREAK) {
				{
				{
				setState(139);
				match(LINE_BREAK);
				}
				}
				setState(144);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StmtContext extends ParserRuleContext {
		public Simple_stmtContext simple_stmt() {
			return getRuleContext(Simple_stmtContext.class,0);
		}
		public Compound_stmtContext compound_stmt() {
			return getRuleContext(Compound_stmtContext.class,0);
		}
		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitStmt(this);
		}
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_stmt);
		try {
			setState(147);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(145);
				simple_stmt();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(146);
				compound_stmt();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Compound_stmtContext extends ParserRuleContext {
		public Compound_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compound_stmt; }
	 
		public Compound_stmtContext() { }
		public void copyFrom(Compound_stmtContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class While_stmtContext extends Compound_stmtContext {
		public TerminalNode WHILE() { return getToken(PythonParser.WHILE, 0); }
		public TestContext test() {
			return getRuleContext(TestContext.class,0);
		}
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public SuiteContext suite() {
			return getRuleContext(SuiteContext.class,0);
		}
		public Else_clauseContext else_clause() {
			return getRuleContext(Else_clauseContext.class,0);
		}
		public While_stmtContext(Compound_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterWhile_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitWhile_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Try_stmtContext extends Compound_stmtContext {
		public TerminalNode TRY() { return getToken(PythonParser.TRY, 0); }
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public SuiteContext suite() {
			return getRuleContext(SuiteContext.class,0);
		}
		public Finally_clauseContext finally_clause() {
			return getRuleContext(Finally_clauseContext.class,0);
		}
		public List<Except_clauseContext> except_clause() {
			return getRuleContexts(Except_clauseContext.class);
		}
		public Except_clauseContext except_clause(int i) {
			return getRuleContext(Except_clauseContext.class,i);
		}
		public Else_clauseContext else_clause() {
			return getRuleContext(Else_clauseContext.class,0);
		}
		public Try_stmtContext(Compound_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterTry_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitTry_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class If_stmtContext extends Compound_stmtContext {
		public TestContext cond;
		public TerminalNode IF() { return getToken(PythonParser.IF, 0); }
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public SuiteContext suite() {
			return getRuleContext(SuiteContext.class,0);
		}
		public TestContext test() {
			return getRuleContext(TestContext.class,0);
		}
		public List<Elif_clauseContext> elif_clause() {
			return getRuleContexts(Elif_clauseContext.class);
		}
		public Elif_clauseContext elif_clause(int i) {
			return getRuleContext(Elif_clauseContext.class,i);
		}
		public Else_clauseContext else_clause() {
			return getRuleContext(Else_clauseContext.class,0);
		}
		public If_stmtContext(Compound_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterIf_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitIf_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class With_stmtContext extends Compound_stmtContext {
		public TerminalNode WITH() { return getToken(PythonParser.WITH, 0); }
		public List<With_itemContext> with_item() {
			return getRuleContexts(With_itemContext.class);
		}
		public With_itemContext with_item(int i) {
			return getRuleContext(With_itemContext.class,i);
		}
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public SuiteContext suite() {
			return getRuleContext(SuiteContext.class,0);
		}
		public TerminalNode ASYNC() { return getToken(PythonParser.ASYNC, 0); }
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public With_stmtContext(Compound_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterWith_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitWith_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Class_or_func_def_stmtContext extends Compound_stmtContext {
		public ClassdefContext classdef() {
			return getRuleContext(ClassdefContext.class,0);
		}
		public FuncdefContext funcdef() {
			return getRuleContext(FuncdefContext.class,0);
		}
		public List<DecoratorContext> decorator() {
			return getRuleContexts(DecoratorContext.class);
		}
		public DecoratorContext decorator(int i) {
			return getRuleContext(DecoratorContext.class,i);
		}
		public Class_or_func_def_stmtContext(Compound_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterClass_or_func_def_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitClass_or_func_def_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class For_stmtContext extends Compound_stmtContext {
		public TerminalNode FOR() { return getToken(PythonParser.FOR, 0); }
		public ExprlistContext exprlist() {
			return getRuleContext(ExprlistContext.class,0);
		}
		public TerminalNode IN() { return getToken(PythonParser.IN, 0); }
		public TestlistContext testlist() {
			return getRuleContext(TestlistContext.class,0);
		}
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public SuiteContext suite() {
			return getRuleContext(SuiteContext.class,0);
		}
		public TerminalNode ASYNC() { return getToken(PythonParser.ASYNC, 0); }
		public Else_clauseContext else_clause() {
			return getRuleContext(Else_clauseContext.class,0);
		}
		public For_stmtContext(Compound_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterFor_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitFor_stmt(this);
		}
	}

	public final Compound_stmtContext compound_stmt() throws RecognitionException {
		Compound_stmtContext _localctx = new Compound_stmtContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_compound_stmt);
		int _la;
		try {
			int _alt;
			setState(223);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				_localctx = new If_stmtContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(149);
				match(IF);
				setState(150);
				((If_stmtContext)_localctx).cond = test();
				setState(151);
				match(COLON);
				setState(152);
				suite();
				setState(156);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(153);
						elif_clause();
						}
						} 
					}
					setState(158);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
				}
				setState(160);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
				case 1:
					{
					setState(159);
					else_clause();
					}
					break;
				}
				}
				break;
			case 2:
				_localctx = new While_stmtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(162);
				match(WHILE);
				setState(163);
				test();
				setState(164);
				match(COLON);
				setState(165);
				suite();
				setState(167);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
				case 1:
					{
					setState(166);
					else_clause();
					}
					break;
				}
				}
				break;
			case 3:
				_localctx = new For_stmtContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(170);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASYNC) {
					{
					setState(169);
					match(ASYNC);
					}
				}

				setState(172);
				match(FOR);
				setState(173);
				exprlist();
				setState(174);
				match(IN);
				setState(175);
				testlist();
				setState(176);
				match(COLON);
				setState(177);
				suite();
				setState(179);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
				case 1:
					{
					setState(178);
					else_clause();
					}
					break;
				}
				}
				break;
			case 4:
				_localctx = new Try_stmtContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(181);
				match(TRY);
				setState(182);
				match(COLON);
				setState(183);
				suite();
				setState(196);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case EXCEPT:
					{
					setState(185); 
					_errHandler.sync(this);
					_alt = 1;
					do {
						switch (_alt) {
						case 1:
							{
							{
							setState(184);
							except_clause();
							}
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(187); 
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
					} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
					setState(190);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
					case 1:
						{
						setState(189);
						else_clause();
						}
						break;
					}
					setState(193);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
					case 1:
						{
						setState(192);
						finally_clause();
						}
						break;
					}
					}
					break;
				case FINALLY:
					{
					setState(195);
					finally_clause();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 5:
				_localctx = new With_stmtContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(199);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASYNC) {
					{
					setState(198);
					match(ASYNC);
					}
				}

				setState(201);
				match(WITH);
				setState(202);
				with_item();
				setState(207);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(203);
					match(COMMA);
					setState(204);
					with_item();
					}
					}
					setState(209);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(210);
				match(COLON);
				setState(211);
				suite();
				}
				break;
			case 6:
				_localctx = new Class_or_func_def_stmtContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(216);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AT) {
					{
					{
					setState(213);
					decorator();
					}
					}
					setState(218);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(221);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case CLASS:
					{
					setState(219);
					classdef();
					}
					break;
				case DEF:
				case ASYNC:
					{
					setState(220);
					funcdef();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SuiteContext extends ParserRuleContext {
		public Simple_stmtContext simple_stmt() {
			return getRuleContext(Simple_stmtContext.class,0);
		}
		public TerminalNode LINE_BREAK() { return getToken(PythonParser.LINE_BREAK, 0); }
		public TerminalNode INDENT() { return getToken(PythonParser.INDENT, 0); }
		public TerminalNode DEDENT() { return getToken(PythonParser.DEDENT, 0); }
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public SuiteContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_suite; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterSuite(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitSuite(this);
		}
	}

	public final SuiteContext suite() throws RecognitionException {
		SuiteContext _localctx = new SuiteContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_suite);
		try {
			int _alt;
			setState(235);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(225);
				simple_stmt();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(226);
				match(LINE_BREAK);
				setState(227);
				match(INDENT);
				setState(229); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(228);
						stmt();
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(231); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(233);
				match(DEDENT);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DecoratorContext extends ParserRuleContext {
		public TerminalNode AT() { return getToken(PythonParser.AT, 0); }
		public Dotted_nameContext dotted_name() {
			return getRuleContext(Dotted_nameContext.class,0);
		}
		public TerminalNode LINE_BREAK() { return getToken(PythonParser.LINE_BREAK, 0); }
		public TerminalNode OPEN_PAREN() { return getToken(PythonParser.OPEN_PAREN, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(PythonParser.CLOSE_PAREN, 0); }
		public ArglistContext arglist() {
			return getRuleContext(ArglistContext.class,0);
		}
		public DecoratorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decorator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterDecorator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitDecorator(this);
		}
	}

	public final DecoratorContext decorator() throws RecognitionException {
		DecoratorContext _localctx = new DecoratorContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_decorator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(237);
			match(AT);
			setState(238);
			dotted_name(0);
			setState(244);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_PAREN) {
				{
				setState(239);
				match(OPEN_PAREN);
				setState(241);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1261322287421849600L) != 0) || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 11007L) != 0)) {
					{
					setState(240);
					arglist();
					}
				}

				setState(243);
				match(CLOSE_PAREN);
				}
			}

			setState(246);
			match(LINE_BREAK);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Elif_clauseContext extends ParserRuleContext {
		public TerminalNode ELIF() { return getToken(PythonParser.ELIF, 0); }
		public TestContext test() {
			return getRuleContext(TestContext.class,0);
		}
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public SuiteContext suite() {
			return getRuleContext(SuiteContext.class,0);
		}
		public Elif_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elif_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterElif_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitElif_clause(this);
		}
	}

	public final Elif_clauseContext elif_clause() throws RecognitionException {
		Elif_clauseContext _localctx = new Elif_clauseContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_elif_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(248);
			match(ELIF);
			setState(249);
			test();
			setState(250);
			match(COLON);
			setState(251);
			suite();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Else_clauseContext extends ParserRuleContext {
		public TerminalNode ELSE() { return getToken(PythonParser.ELSE, 0); }
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public SuiteContext suite() {
			return getRuleContext(SuiteContext.class,0);
		}
		public Else_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_else_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterElse_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitElse_clause(this);
		}
	}

	public final Else_clauseContext else_clause() throws RecognitionException {
		Else_clauseContext _localctx = new Else_clauseContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_else_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(253);
			match(ELSE);
			setState(254);
			match(COLON);
			setState(255);
			suite();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Finally_clauseContext extends ParserRuleContext {
		public TerminalNode FINALLY() { return getToken(PythonParser.FINALLY, 0); }
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public SuiteContext suite() {
			return getRuleContext(SuiteContext.class,0);
		}
		public Finally_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_finally_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterFinally_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitFinally_clause(this);
		}
	}

	public final Finally_clauseContext finally_clause() throws RecognitionException {
		Finally_clauseContext _localctx = new Finally_clauseContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_finally_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(257);
			match(FINALLY);
			setState(258);
			match(COLON);
			setState(259);
			suite();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class With_itemContext extends ParserRuleContext {
		public TestContext test() {
			return getRuleContext(TestContext.class,0);
		}
		public TerminalNode AS() { return getToken(PythonParser.AS, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public With_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_with_item; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterWith_item(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitWith_item(this);
		}
	}

	public final With_itemContext with_item() throws RecognitionException {
		With_itemContext _localctx = new With_itemContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_with_item);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(261);
			test();
			setState(264);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(262);
				match(AS);
				setState(263);
				expr(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Except_clauseContext extends ParserRuleContext {
		public TerminalNode EXCEPT() { return getToken(PythonParser.EXCEPT, 0); }
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public SuiteContext suite() {
			return getRuleContext(SuiteContext.class,0);
		}
		public TestContext test() {
			return getRuleContext(TestContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(PythonParser.COMMA, 0); }
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode AS() { return getToken(PythonParser.AS, 0); }
		public Except_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_except_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterExcept_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitExcept_clause(this);
		}
	}

	public final Except_clauseContext except_clause() throws RecognitionException {
		Except_clauseContext _localctx = new Except_clauseContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_except_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(266);
			match(EXCEPT);
			setState(280);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1261023220259094528L) != 0) || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 11007L) != 0)) {
				{
				setState(267);
				test();
				setState(278);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
				case 1:
					{
					setState(268);
					if (!(this.CheckVersion(2))) throw new FailedPredicateException(this, "this.CheckVersion(2)");
					setState(269);
					match(COMMA);
					setState(270);
					name();
					this.SetVersion(2);
					}
					break;
				case 2:
					{
					setState(273);
					if (!(this.CheckVersion(3))) throw new FailedPredicateException(this, "this.CheckVersion(3)");
					setState(274);
					match(AS);
					setState(275);
					name();
					this.SetVersion(3);
					}
					break;
				}
				}
			}

			setState(282);
			match(COLON);
			setState(283);
			suite();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ClassdefContext extends ParserRuleContext {
		public TerminalNode CLASS() { return getToken(PythonParser.CLASS, 0); }
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public SuiteContext suite() {
			return getRuleContext(SuiteContext.class,0);
		}
		public TerminalNode OPEN_PAREN() { return getToken(PythonParser.OPEN_PAREN, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(PythonParser.CLOSE_PAREN, 0); }
		public ArglistContext arglist() {
			return getRuleContext(ArglistContext.class,0);
		}
		public ClassdefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classdef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterClassdef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitClassdef(this);
		}
	}

	public final ClassdefContext classdef() throws RecognitionException {
		ClassdefContext _localctx = new ClassdefContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_classdef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(285);
			match(CLASS);
			setState(286);
			name();
			setState(292);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_PAREN) {
				{
				setState(287);
				match(OPEN_PAREN);
				setState(289);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1261322287421849600L) != 0) || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 11007L) != 0)) {
					{
					setState(288);
					arglist();
					}
				}

				setState(291);
				match(CLOSE_PAREN);
				}
			}

			setState(294);
			match(COLON);
			setState(295);
			suite();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FuncdefContext extends ParserRuleContext {
		public TerminalNode DEF() { return getToken(PythonParser.DEF, 0); }
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode OPEN_PAREN() { return getToken(PythonParser.OPEN_PAREN, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(PythonParser.CLOSE_PAREN, 0); }
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public SuiteContext suite() {
			return getRuleContext(SuiteContext.class,0);
		}
		public TerminalNode ASYNC() { return getToken(PythonParser.ASYNC, 0); }
		public TypedargslistContext typedargslist() {
			return getRuleContext(TypedargslistContext.class,0);
		}
		public TerminalNode ARROW() { return getToken(PythonParser.ARROW, 0); }
		public TestContext test() {
			return getRuleContext(TestContext.class,0);
		}
		public FuncdefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcdef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterFuncdef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitFuncdef(this);
		}
	}

	public final FuncdefContext funcdef() throws RecognitionException {
		FuncdefContext _localctx = new FuncdefContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_funcdef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASYNC) {
				{
				setState(297);
				match(ASYNC);
				}
			}

			setState(300);
			match(DEF);
			setState(301);
			name();
			setState(302);
			match(OPEN_PAREN);
			setState(304);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 39)) & ~0x3f) == 0 && ((1L << (_la - 39)) & 144115188075856419L) != 0)) {
				{
				setState(303);
				typedargslist();
				}
			}

			setState(306);
			match(CLOSE_PAREN);
			setState(309);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ARROW) {
				{
				setState(307);
				match(ARROW);
				setState(308);
				test();
				}
			}

			setState(311);
			match(COLON);
			setState(312);
			suite();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypedargslistContext extends ParserRuleContext {
		public ArgsContext args() {
			return getRuleContext(ArgsContext.class,0);
		}
		public KwargsContext kwargs() {
			return getRuleContext(KwargsContext.class,0);
		}
		public List<Def_parametersContext> def_parameters() {
			return getRuleContexts(Def_parametersContext.class);
		}
		public Def_parametersContext def_parameters(int i) {
			return getRuleContext(Def_parametersContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public TypedargslistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typedargslist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterTypedargslist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitTypedargslist(this);
		}
	}

	public final TypedargslistContext typedargslist() throws RecognitionException {
		TypedargslistContext _localctx = new TypedargslistContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_typedargslist);
		int _la;
		try {
			setState(338);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(317);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
				case 1:
					{
					setState(314);
					def_parameters();
					setState(315);
					match(COMMA);
					}
					break;
				}
				setState(329);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case STAR:
					{
					setState(319);
					args();
					setState(322);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
					case 1:
						{
						setState(320);
						match(COMMA);
						setState(321);
						def_parameters();
						}
						break;
					}
					setState(326);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
					case 1:
						{
						setState(324);
						match(COMMA);
						setState(325);
						kwargs();
						}
						break;
					}
					}
					break;
				case POWER:
					{
					setState(328);
					kwargs();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(332);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(331);
					match(COMMA);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(334);
				def_parameters();
				setState(336);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(335);
					match(COMMA);
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgsContext extends ParserRuleContext {
		public TerminalNode STAR() { return getToken(PythonParser.STAR, 0); }
		public Named_parameterContext named_parameter() {
			return getRuleContext(Named_parameterContext.class,0);
		}
		public ArgsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_args; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterArgs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitArgs(this);
		}
	}

	public final ArgsContext args() throws RecognitionException {
		ArgsContext _localctx = new ArgsContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_args);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(340);
			match(STAR);
			setState(341);
			named_parameter();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class KwargsContext extends ParserRuleContext {
		public TerminalNode POWER() { return getToken(PythonParser.POWER, 0); }
		public Named_parameterContext named_parameter() {
			return getRuleContext(Named_parameterContext.class,0);
		}
		public KwargsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kwargs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterKwargs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitKwargs(this);
		}
	}

	public final KwargsContext kwargs() throws RecognitionException {
		KwargsContext _localctx = new KwargsContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_kwargs);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(343);
			match(POWER);
			setState(344);
			named_parameter();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Def_parametersContext extends ParserRuleContext {
		public List<Def_parameterContext> def_parameter() {
			return getRuleContexts(Def_parameterContext.class);
		}
		public Def_parameterContext def_parameter(int i) {
			return getRuleContext(Def_parameterContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public Def_parametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_def_parameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterDef_parameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitDef_parameters(this);
		}
	}

	public final Def_parametersContext def_parameters() throws RecognitionException {
		Def_parametersContext _localctx = new Def_parametersContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_def_parameters);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(346);
			def_parameter();
			setState(351);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,39,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(347);
					match(COMMA);
					setState(348);
					def_parameter();
					}
					} 
				}
				setState(353);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,39,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Def_parameterContext extends ParserRuleContext {
		public Named_parameterContext named_parameter() {
			return getRuleContext(Named_parameterContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(PythonParser.ASSIGN, 0); }
		public TestContext test() {
			return getRuleContext(TestContext.class,0);
		}
		public TerminalNode STAR() { return getToken(PythonParser.STAR, 0); }
		public Def_parameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_def_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterDef_parameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitDef_parameter(this);
		}
	}

	public final Def_parameterContext def_parameter() throws RecognitionException {
		Def_parameterContext _localctx = new Def_parameterContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_def_parameter);
		int _la;
		try {
			setState(360);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TRUE:
			case FALSE:
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(354);
				named_parameter();
				setState(357);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASSIGN) {
					{
					setState(355);
					match(ASSIGN);
					setState(356);
					test();
					}
				}

				}
				break;
			case STAR:
				enterOuterAlt(_localctx, 2);
				{
				setState(359);
				match(STAR);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Named_parameterContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public TestContext test() {
			return getRuleContext(TestContext.class,0);
		}
		public Named_parameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_named_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterNamed_parameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitNamed_parameter(this);
		}
	}

	public final Named_parameterContext named_parameter() throws RecognitionException {
		Named_parameterContext _localctx = new Named_parameterContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_named_parameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(362);
			name();
			setState(365);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(363);
				match(COLON);
				setState(364);
				test();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Simple_stmtContext extends ParserRuleContext {
		public List<Small_stmtContext> small_stmt() {
			return getRuleContexts(Small_stmtContext.class);
		}
		public Small_stmtContext small_stmt(int i) {
			return getRuleContext(Small_stmtContext.class,i);
		}
		public TerminalNode LINE_BREAK() { return getToken(PythonParser.LINE_BREAK, 0); }
		public TerminalNode EOF() { return getToken(PythonParser.EOF, 0); }
		public List<TerminalNode> SEMI_COLON() { return getTokens(PythonParser.SEMI_COLON); }
		public TerminalNode SEMI_COLON(int i) {
			return getToken(PythonParser.SEMI_COLON, i);
		}
		public Simple_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simple_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterSimple_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitSimple_stmt(this);
		}
	}

	public final Simple_stmtContext simple_stmt() throws RecognitionException {
		Simple_stmtContext _localctx = new Simple_stmtContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_simple_stmt);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(367);
			small_stmt();
			setState(372);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,43,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(368);
					match(SEMI_COLON);
					setState(369);
					small_stmt();
					}
					} 
				}
				setState(374);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,43,_ctx);
			}
			setState(376);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI_COLON) {
				{
				setState(375);
				match(SEMI_COLON);
				}
			}

			setState(378);
			_la = _input.LA(1);
			if ( !(_la==EOF || _la==LINE_BREAK) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Small_stmtContext extends ParserRuleContext {
		public Small_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_small_stmt; }
	 
		public Small_stmtContext() { }
		public void copyFrom(Small_stmtContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Assert_stmtContext extends Small_stmtContext {
		public TerminalNode ASSERT() { return getToken(PythonParser.ASSERT, 0); }
		public List<TestContext> test() {
			return getRuleContexts(TestContext.class);
		}
		public TestContext test(int i) {
			return getRuleContext(TestContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(PythonParser.COMMA, 0); }
		public Assert_stmtContext(Small_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterAssert_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitAssert_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Nonlocal_stmtContext extends Small_stmtContext {
		public TerminalNode NONLOCAL() { return getToken(PythonParser.NONLOCAL, 0); }
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public Nonlocal_stmtContext(Small_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterNonlocal_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitNonlocal_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Pass_stmtContext extends Small_stmtContext {
		public TerminalNode PASS() { return getToken(PythonParser.PASS, 0); }
		public Pass_stmtContext(Small_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterPass_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitPass_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Import_stmtContext extends Small_stmtContext {
		public TerminalNode IMPORT() { return getToken(PythonParser.IMPORT, 0); }
		public Dotted_as_namesContext dotted_as_names() {
			return getRuleContext(Dotted_as_namesContext.class,0);
		}
		public Import_stmtContext(Small_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterImport_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitImport_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Expr_stmtContext extends Small_stmtContext {
		public Testlist_star_exprContext testlist_star_expr() {
			return getRuleContext(Testlist_star_exprContext.class,0);
		}
		public Assign_partContext assign_part() {
			return getRuleContext(Assign_partContext.class,0);
		}
		public Expr_stmtContext(Small_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterExpr_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitExpr_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Raise_stmtContext extends Small_stmtContext {
		public TerminalNode RAISE() { return getToken(PythonParser.RAISE, 0); }
		public List<TestContext> test() {
			return getRuleContexts(TestContext.class);
		}
		public TestContext test(int i) {
			return getRuleContext(TestContext.class,i);
		}
		public TerminalNode FROM() { return getToken(PythonParser.FROM, 0); }
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public Raise_stmtContext(Small_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterRaise_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitRaise_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Yield_stmtContext extends Small_stmtContext {
		public Yield_exprContext yield_expr() {
			return getRuleContext(Yield_exprContext.class,0);
		}
		public Yield_stmtContext(Small_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterYield_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitYield_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class From_stmtContext extends Small_stmtContext {
		public TerminalNode FROM() { return getToken(PythonParser.FROM, 0); }
		public TerminalNode IMPORT() { return getToken(PythonParser.IMPORT, 0); }
		public Dotted_nameContext dotted_name() {
			return getRuleContext(Dotted_nameContext.class,0);
		}
		public TerminalNode STAR() { return getToken(PythonParser.STAR, 0); }
		public TerminalNode OPEN_PAREN() { return getToken(PythonParser.OPEN_PAREN, 0); }
		public Import_as_namesContext import_as_names() {
			return getRuleContext(Import_as_namesContext.class,0);
		}
		public TerminalNode CLOSE_PAREN() { return getToken(PythonParser.CLOSE_PAREN, 0); }
		public List<TerminalNode> DOT() { return getTokens(PythonParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(PythonParser.DOT, i);
		}
		public List<TerminalNode> ELLIPSIS() { return getTokens(PythonParser.ELLIPSIS); }
		public TerminalNode ELLIPSIS(int i) {
			return getToken(PythonParser.ELLIPSIS, i);
		}
		public From_stmtContext(Small_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterFrom_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitFrom_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Global_stmtContext extends Small_stmtContext {
		public TerminalNode GLOBAL() { return getToken(PythonParser.GLOBAL, 0); }
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public Global_stmtContext(Small_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterGlobal_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitGlobal_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Continue_stmtContext extends Small_stmtContext {
		public TerminalNode CONTINUE() { return getToken(PythonParser.CONTINUE, 0); }
		public Continue_stmtContext(Small_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterContinue_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitContinue_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Exec_stmtContext extends Small_stmtContext {
		public TerminalNode EXEC() { return getToken(PythonParser.EXEC, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode IN() { return getToken(PythonParser.IN, 0); }
		public List<TestContext> test() {
			return getRuleContexts(TestContext.class);
		}
		public TestContext test(int i) {
			return getRuleContext(TestContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(PythonParser.COMMA, 0); }
		public Exec_stmtContext(Small_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterExec_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitExec_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Break_stmtContext extends Small_stmtContext {
		public TerminalNode BREAK() { return getToken(PythonParser.BREAK, 0); }
		public Break_stmtContext(Small_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterBreak_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitBreak_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Del_stmtContext extends Small_stmtContext {
		public TerminalNode DEL() { return getToken(PythonParser.DEL, 0); }
		public ExprlistContext exprlist() {
			return getRuleContext(ExprlistContext.class,0);
		}
		public Del_stmtContext(Small_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterDel_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitDel_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Print_stmtContext extends Small_stmtContext {
		public TerminalNode PRINT() { return getToken(PythonParser.PRINT, 0); }
		public TerminalNode RIGHT_SHIFT() { return getToken(PythonParser.RIGHT_SHIFT, 0); }
		public List<TestContext> test() {
			return getRuleContexts(TestContext.class);
		}
		public TestContext test(int i) {
			return getRuleContext(TestContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public Print_stmtContext(Small_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterPrint_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitPrint_stmt(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Return_stmtContext extends Small_stmtContext {
		public TerminalNode RETURN() { return getToken(PythonParser.RETURN, 0); }
		public TestlistContext testlist() {
			return getRuleContext(TestlistContext.class,0);
		}
		public Return_stmtContext(Small_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterReturn_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitReturn_stmt(this);
		}
	}

	public final Small_stmtContext small_stmt() throws RecognitionException {
		Small_stmtContext _localctx = new Small_stmtContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_small_stmt);
		int _la;
		try {
			int _alt;
			setState(504);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
			case 1:
				_localctx = new Expr_stmtContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(380);
				testlist_star_expr();
				setState(382);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
				case 1:
					{
					setState(381);
					assign_part();
					}
					break;
				}
				}
				break;
			case 2:
				_localctx = new Print_stmtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(384);
				if (!(this.CheckVersion(2))) throw new FailedPredicateException(this, "this.CheckVersion(2)");
				setState(385);
				match(PRINT);
				setState(408);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NONE:
				case LAMBDA:
				case NOT:
				case AWAIT:
				case PRINT:
				case EXEC:
				case TRUE:
				case FALSE:
				case ELLIPSIS:
				case REVERSE_QUOTE:
				case ADD:
				case MINUS:
				case NOT_OP:
				case STRING:
				case DECIMAL_INTEGER:
				case OCT_INTEGER:
				case HEX_INTEGER:
				case BIN_INTEGER:
				case IMAG_NUMBER:
				case FLOAT_NUMBER:
				case OPEN_PAREN:
				case OPEN_BRACE:
				case OPEN_BRACKET:
				case NAME:
					{
					{
					setState(386);
					test();
					setState(391);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,46,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(387);
							match(COMMA);
							setState(388);
							test();
							}
							} 
						}
						setState(393);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,46,_ctx);
					}
					setState(395);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(394);
						match(COMMA);
						}
					}

					}
					}
					break;
				case RIGHT_SHIFT:
					{
					setState(397);
					match(RIGHT_SHIFT);
					setState(398);
					test();
					{
					setState(401); 
					_errHandler.sync(this);
					_alt = 1;
					do {
						switch (_alt) {
						case 1:
							{
							{
							setState(399);
							match(COMMA);
							setState(400);
							test();
							}
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(403); 
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
					} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
					setState(406);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(405);
						match(COMMA);
						}
					}

					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				this.SetVersion(2);
				}
				break;
			case 3:
				_localctx = new Del_stmtContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(412);
				match(DEL);
				setState(413);
				exprlist();
				}
				break;
			case 4:
				_localctx = new Pass_stmtContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(414);
				match(PASS);
				}
				break;
			case 5:
				_localctx = new Break_stmtContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(415);
				match(BREAK);
				}
				break;
			case 6:
				_localctx = new Continue_stmtContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(416);
				match(CONTINUE);
				}
				break;
			case 7:
				_localctx = new Return_stmtContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(417);
				match(RETURN);
				setState(419);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1261023220259094528L) != 0) || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 11007L) != 0)) {
					{
					setState(418);
					testlist();
					}
				}

				}
				break;
			case 8:
				_localctx = new Raise_stmtContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(421);
				match(RAISE);
				setState(431);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1261023220259094528L) != 0) || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 11007L) != 0)) {
					{
					setState(422);
					test();
					setState(429);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(423);
						match(COMMA);
						setState(424);
						test();
						setState(427);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==COMMA) {
							{
							setState(425);
							match(COMMA);
							setState(426);
							test();
							}
						}

						}
					}

					}
				}

				setState(435);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FROM) {
					{
					setState(433);
					match(FROM);
					setState(434);
					test();
					}
				}

				}
				break;
			case 9:
				_localctx = new Yield_stmtContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(437);
				yield_expr();
				}
				break;
			case 10:
				_localctx = new Import_stmtContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(438);
				match(IMPORT);
				setState(439);
				dotted_as_names();
				}
				break;
			case 11:
				_localctx = new From_stmtContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(440);
				match(FROM);
				setState(453);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
				case 1:
					{
					setState(444);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==DOT || _la==ELLIPSIS) {
						{
						{
						setState(441);
						_la = _input.LA(1);
						if ( !(_la==DOT || _la==ELLIPSIS) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						}
						setState(446);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(447);
					dotted_name(0);
					}
					break;
				case 2:
					{
					setState(449); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(448);
						_la = _input.LA(1);
						if ( !(_la==DOT || _la==ELLIPSIS) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						}
						setState(451); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==DOT || _la==ELLIPSIS );
					}
					break;
				}
				setState(455);
				match(IMPORT);
				setState(462);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case STAR:
					{
					setState(456);
					match(STAR);
					}
					break;
				case OPEN_PAREN:
					{
					setState(457);
					match(OPEN_PAREN);
					setState(458);
					import_as_names();
					setState(459);
					match(CLOSE_PAREN);
					}
					break;
				case TRUE:
				case FALSE:
				case NAME:
					{
					setState(461);
					import_as_names();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 12:
				_localctx = new Global_stmtContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(464);
				match(GLOBAL);
				setState(465);
				name();
				setState(470);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(466);
					match(COMMA);
					setState(467);
					name();
					}
					}
					setState(472);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 13:
				_localctx = new Exec_stmtContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(473);
				if (!(this.CheckVersion(2))) throw new FailedPredicateException(this, "this.CheckVersion(2)");
				setState(474);
				match(EXEC);
				setState(475);
				expr(0);
				setState(482);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IN) {
					{
					setState(476);
					match(IN);
					setState(477);
					test();
					setState(480);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(478);
						match(COMMA);
						setState(479);
						test();
						}
					}

					}
				}

				this.SetVersion(2);
				}
				break;
			case 14:
				_localctx = new Assert_stmtContext(_localctx);
				enterOuterAlt(_localctx, 14);
				{
				setState(486);
				match(ASSERT);
				setState(487);
				test();
				setState(490);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(488);
					match(COMMA);
					setState(489);
					test();
					}
				}

				}
				break;
			case 15:
				_localctx = new Nonlocal_stmtContext(_localctx);
				enterOuterAlt(_localctx, 15);
				{
				setState(492);
				if (!(this.CheckVersion(3))) throw new FailedPredicateException(this, "this.CheckVersion(3)");
				setState(493);
				match(NONLOCAL);
				setState(494);
				name();
				setState(499);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(495);
					match(COMMA);
					setState(496);
					name();
					}
					}
					setState(501);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				this.SetVersion(3);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Testlist_star_exprContext extends ParserRuleContext {
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public List<TestContext> test() {
			return getRuleContexts(TestContext.class);
		}
		public TestContext test(int i) {
			return getRuleContext(TestContext.class,i);
		}
		public List<Star_exprContext> star_expr() {
			return getRuleContexts(Star_exprContext.class);
		}
		public Star_exprContext star_expr(int i) {
			return getRuleContext(Star_exprContext.class,i);
		}
		public TestlistContext testlist() {
			return getRuleContext(TestlistContext.class,0);
		}
		public Testlist_star_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_testlist_star_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterTestlist_star_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitTestlist_star_expr(this);
		}
	}

	public final Testlist_star_exprContext testlist_star_expr() throws RecognitionException {
		Testlist_star_exprContext _localctx = new Testlist_star_exprContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_testlist_star_expr);
		try {
			int _alt;
			setState(521);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(512); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(508);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case NONE:
						case LAMBDA:
						case NOT:
						case AWAIT:
						case PRINT:
						case EXEC:
						case TRUE:
						case FALSE:
						case ELLIPSIS:
						case REVERSE_QUOTE:
						case ADD:
						case MINUS:
						case NOT_OP:
						case STRING:
						case DECIMAL_INTEGER:
						case OCT_INTEGER:
						case HEX_INTEGER:
						case BIN_INTEGER:
						case IMAG_NUMBER:
						case FLOAT_NUMBER:
						case OPEN_PAREN:
						case OPEN_BRACE:
						case OPEN_BRACKET:
						case NAME:
							{
							setState(506);
							test();
							}
							break;
						case STAR:
							{
							setState(507);
							star_expr();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(510);
						match(COMMA);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(514); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,67,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(518);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
				case 1:
					{
					setState(516);
					test();
					}
					break;
				case 2:
					{
					setState(517);
					star_expr();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(520);
				testlist();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Star_exprContext extends ParserRuleContext {
		public TerminalNode STAR() { return getToken(PythonParser.STAR, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Star_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_star_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterStar_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitStar_expr(this);
		}
	}

	public final Star_exprContext star_expr() throws RecognitionException {
		Star_exprContext _localctx = new Star_exprContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_star_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(523);
			match(STAR);
			setState(524);
			expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Assign_partContext extends ParserRuleContext {
		public Token op;
		public List<TerminalNode> ASSIGN() { return getTokens(PythonParser.ASSIGN); }
		public TerminalNode ASSIGN(int i) {
			return getToken(PythonParser.ASSIGN, i);
		}
		public List<Testlist_star_exprContext> testlist_star_expr() {
			return getRuleContexts(Testlist_star_exprContext.class);
		}
		public Testlist_star_exprContext testlist_star_expr(int i) {
			return getRuleContext(Testlist_star_exprContext.class,i);
		}
		public Yield_exprContext yield_expr() {
			return getRuleContext(Yield_exprContext.class,0);
		}
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public TestContext test() {
			return getRuleContext(TestContext.class,0);
		}
		public TestlistContext testlist() {
			return getRuleContext(TestlistContext.class,0);
		}
		public TerminalNode ADD_ASSIGN() { return getToken(PythonParser.ADD_ASSIGN, 0); }
		public TerminalNode SUB_ASSIGN() { return getToken(PythonParser.SUB_ASSIGN, 0); }
		public TerminalNode MULT_ASSIGN() { return getToken(PythonParser.MULT_ASSIGN, 0); }
		public TerminalNode AT_ASSIGN() { return getToken(PythonParser.AT_ASSIGN, 0); }
		public TerminalNode DIV_ASSIGN() { return getToken(PythonParser.DIV_ASSIGN, 0); }
		public TerminalNode MOD_ASSIGN() { return getToken(PythonParser.MOD_ASSIGN, 0); }
		public TerminalNode AND_ASSIGN() { return getToken(PythonParser.AND_ASSIGN, 0); }
		public TerminalNode OR_ASSIGN() { return getToken(PythonParser.OR_ASSIGN, 0); }
		public TerminalNode XOR_ASSIGN() { return getToken(PythonParser.XOR_ASSIGN, 0); }
		public TerminalNode LEFT_SHIFT_ASSIGN() { return getToken(PythonParser.LEFT_SHIFT_ASSIGN, 0); }
		public TerminalNode RIGHT_SHIFT_ASSIGN() { return getToken(PythonParser.RIGHT_SHIFT_ASSIGN, 0); }
		public TerminalNode POWER_ASSIGN() { return getToken(PythonParser.POWER_ASSIGN, 0); }
		public TerminalNode IDIV_ASSIGN() { return getToken(PythonParser.IDIV_ASSIGN, 0); }
		public Assign_partContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign_part; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterAssign_part(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitAssign_part(this);
		}
	}

	public final Assign_partContext assign_part() throws RecognitionException {
		Assign_partContext _localctx = new Assign_partContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_assign_part);
		int _la;
		try {
			int _alt;
			setState(556);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(526);
				match(ASSIGN);
				setState(540);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NONE:
				case LAMBDA:
				case NOT:
				case AWAIT:
				case PRINT:
				case EXEC:
				case TRUE:
				case FALSE:
				case ELLIPSIS:
				case REVERSE_QUOTE:
				case STAR:
				case ADD:
				case MINUS:
				case NOT_OP:
				case STRING:
				case DECIMAL_INTEGER:
				case OCT_INTEGER:
				case HEX_INTEGER:
				case BIN_INTEGER:
				case IMAG_NUMBER:
				case FLOAT_NUMBER:
				case OPEN_PAREN:
				case OPEN_BRACE:
				case OPEN_BRACKET:
				case NAME:
					{
					setState(527);
					testlist_star_expr();
					setState(532);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,70,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(528);
							match(ASSIGN);
							setState(529);
							testlist_star_expr();
							}
							} 
						}
						setState(534);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,70,_ctx);
					}
					setState(537);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ASSIGN) {
						{
						setState(535);
						match(ASSIGN);
						setState(536);
						yield_expr();
						}
					}

					}
					break;
				case YIELD:
					{
					setState(539);
					yield_expr();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(542);
				if (!(this.CheckVersion(3))) throw new FailedPredicateException(this, "this.CheckVersion(3)");
				setState(543);
				match(COLON);
				setState(544);
				test();
				setState(547);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASSIGN) {
					{
					setState(545);
					match(ASSIGN);
					setState(546);
					testlist();
					}
				}

				this.SetVersion(3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(551);
				((Assign_partContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & 8191L) != 0)) ) {
					((Assign_partContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(554);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case YIELD:
					{
					setState(552);
					yield_expr();
					}
					break;
				case NONE:
				case LAMBDA:
				case NOT:
				case AWAIT:
				case PRINT:
				case EXEC:
				case TRUE:
				case FALSE:
				case ELLIPSIS:
				case REVERSE_QUOTE:
				case ADD:
				case MINUS:
				case NOT_OP:
				case STRING:
				case DECIMAL_INTEGER:
				case OCT_INTEGER:
				case HEX_INTEGER:
				case BIN_INTEGER:
				case IMAG_NUMBER:
				case FLOAT_NUMBER:
				case OPEN_PAREN:
				case OPEN_BRACE:
				case OPEN_BRACKET:
				case NAME:
					{
					setState(553);
					testlist();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExprlistContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public ExprlistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprlist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterExprlist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitExprlist(this);
		}
	}

	public final ExprlistContext exprlist() throws RecognitionException {
		ExprlistContext _localctx = new ExprlistContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_exprlist);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(558);
			expr(0);
			setState(563);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,76,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(559);
					match(COMMA);
					setState(560);
					expr(0);
					}
					} 
				}
				setState(565);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,76,_ctx);
			}
			setState(567);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(566);
				match(COMMA);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Import_as_namesContext extends ParserRuleContext {
		public List<Import_as_nameContext> import_as_name() {
			return getRuleContexts(Import_as_nameContext.class);
		}
		public Import_as_nameContext import_as_name(int i) {
			return getRuleContext(Import_as_nameContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public Import_as_namesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_import_as_names; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterImport_as_names(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitImport_as_names(this);
		}
	}

	public final Import_as_namesContext import_as_names() throws RecognitionException {
		Import_as_namesContext _localctx = new Import_as_namesContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_import_as_names);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(569);
			import_as_name();
			setState(574);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(570);
					match(COMMA);
					setState(571);
					import_as_name();
					}
					} 
				}
				setState(576);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
			}
			setState(578);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(577);
				match(COMMA);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Import_as_nameContext extends ParserRuleContext {
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public TerminalNode AS() { return getToken(PythonParser.AS, 0); }
		public Import_as_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_import_as_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterImport_as_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitImport_as_name(this);
		}
	}

	public final Import_as_nameContext import_as_name() throws RecognitionException {
		Import_as_nameContext _localctx = new Import_as_nameContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_import_as_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(580);
			name();
			setState(583);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(581);
				match(AS);
				setState(582);
				name();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Dotted_as_namesContext extends ParserRuleContext {
		public List<Dotted_as_nameContext> dotted_as_name() {
			return getRuleContexts(Dotted_as_nameContext.class);
		}
		public Dotted_as_nameContext dotted_as_name(int i) {
			return getRuleContext(Dotted_as_nameContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public Dotted_as_namesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dotted_as_names; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterDotted_as_names(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitDotted_as_names(this);
		}
	}

	public final Dotted_as_namesContext dotted_as_names() throws RecognitionException {
		Dotted_as_namesContext _localctx = new Dotted_as_namesContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_dotted_as_names);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(585);
			dotted_as_name();
			setState(590);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(586);
				match(COMMA);
				setState(587);
				dotted_as_name();
				}
				}
				setState(592);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Dotted_as_nameContext extends ParserRuleContext {
		public Dotted_nameContext dotted_name() {
			return getRuleContext(Dotted_nameContext.class,0);
		}
		public TerminalNode AS() { return getToken(PythonParser.AS, 0); }
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public Dotted_as_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dotted_as_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterDotted_as_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitDotted_as_name(this);
		}
	}

	public final Dotted_as_nameContext dotted_as_name() throws RecognitionException {
		Dotted_as_nameContext _localctx = new Dotted_as_nameContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_dotted_as_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(593);
			dotted_name(0);
			setState(596);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(594);
				match(AS);
				setState(595);
				name();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TestContext extends ParserRuleContext {
		public List<Logical_testContext> logical_test() {
			return getRuleContexts(Logical_testContext.class);
		}
		public Logical_testContext logical_test(int i) {
			return getRuleContext(Logical_testContext.class,i);
		}
		public TerminalNode IF() { return getToken(PythonParser.IF, 0); }
		public TerminalNode ELSE() { return getToken(PythonParser.ELSE, 0); }
		public TestContext test() {
			return getRuleContext(TestContext.class,0);
		}
		public TerminalNode LAMBDA() { return getToken(PythonParser.LAMBDA, 0); }
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public VarargslistContext varargslist() {
			return getRuleContext(VarargslistContext.class,0);
		}
		public TestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_test; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterTest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitTest(this);
		}
	}

	public final TestContext test() throws RecognitionException {
		TestContext _localctx = new TestContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_test);
		int _la;
		try {
			setState(612);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NONE:
			case NOT:
			case AWAIT:
			case PRINT:
			case EXEC:
			case TRUE:
			case FALSE:
			case ELLIPSIS:
			case REVERSE_QUOTE:
			case ADD:
			case MINUS:
			case NOT_OP:
			case STRING:
			case DECIMAL_INTEGER:
			case OCT_INTEGER:
			case HEX_INTEGER:
			case BIN_INTEGER:
			case IMAG_NUMBER:
			case FLOAT_NUMBER:
			case OPEN_PAREN:
			case OPEN_BRACE:
			case OPEN_BRACKET:
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(598);
				logical_test(0);
				setState(604);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,83,_ctx) ) {
				case 1:
					{
					setState(599);
					match(IF);
					setState(600);
					logical_test(0);
					setState(601);
					match(ELSE);
					setState(602);
					test();
					}
					break;
				}
				}
				break;
			case LAMBDA:
				enterOuterAlt(_localctx, 2);
				{
				setState(606);
				match(LAMBDA);
				setState(608);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 39)) & ~0x3f) == 0 && ((1L << (_la - 39)) & 144115188075856419L) != 0)) {
					{
					setState(607);
					varargslist();
					}
				}

				setState(610);
				match(COLON);
				setState(611);
				test();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VarargslistContext extends ParserRuleContext {
		public VarargsContext varargs() {
			return getRuleContext(VarargsContext.class,0);
		}
		public VarkwargsContext varkwargs() {
			return getRuleContext(VarkwargsContext.class,0);
		}
		public List<Vardef_parametersContext> vardef_parameters() {
			return getRuleContexts(Vardef_parametersContext.class);
		}
		public Vardef_parametersContext vardef_parameters(int i) {
			return getRuleContext(Vardef_parametersContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public VarargslistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varargslist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterVarargslist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitVarargslist(this);
		}
	}

	public final VarargslistContext varargslist() throws RecognitionException {
		VarargslistContext _localctx = new VarargslistContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_varargslist);
		int _la;
		try {
			setState(638);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,92,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(617);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
				case 1:
					{
					setState(614);
					vardef_parameters();
					setState(615);
					match(COMMA);
					}
					break;
				}
				setState(629);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case STAR:
					{
					setState(619);
					varargs();
					setState(622);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
					case 1:
						{
						setState(620);
						match(COMMA);
						setState(621);
						vardef_parameters();
						}
						break;
					}
					setState(626);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,88,_ctx) ) {
					case 1:
						{
						setState(624);
						match(COMMA);
						setState(625);
						varkwargs();
						}
						break;
					}
					}
					break;
				case POWER:
					{
					setState(628);
					varkwargs();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(632);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(631);
					match(COMMA);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(634);
				vardef_parameters();
				setState(636);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(635);
					match(COMMA);
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Vardef_parametersContext extends ParserRuleContext {
		public List<Vardef_parameterContext> vardef_parameter() {
			return getRuleContexts(Vardef_parameterContext.class);
		}
		public Vardef_parameterContext vardef_parameter(int i) {
			return getRuleContext(Vardef_parameterContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public Vardef_parametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vardef_parameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterVardef_parameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitVardef_parameters(this);
		}
	}

	public final Vardef_parametersContext vardef_parameters() throws RecognitionException {
		Vardef_parametersContext _localctx = new Vardef_parametersContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_vardef_parameters);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(640);
			vardef_parameter();
			setState(645);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,93,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(641);
					match(COMMA);
					setState(642);
					vardef_parameter();
					}
					} 
				}
				setState(647);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,93,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Vardef_parameterContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(PythonParser.ASSIGN, 0); }
		public TestContext test() {
			return getRuleContext(TestContext.class,0);
		}
		public TerminalNode STAR() { return getToken(PythonParser.STAR, 0); }
		public Vardef_parameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vardef_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterVardef_parameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitVardef_parameter(this);
		}
	}

	public final Vardef_parameterContext vardef_parameter() throws RecognitionException {
		Vardef_parameterContext _localctx = new Vardef_parameterContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_vardef_parameter);
		int _la;
		try {
			setState(654);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TRUE:
			case FALSE:
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(648);
				name();
				setState(651);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASSIGN) {
					{
					setState(649);
					match(ASSIGN);
					setState(650);
					test();
					}
				}

				}
				break;
			case STAR:
				enterOuterAlt(_localctx, 2);
				{
				setState(653);
				match(STAR);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VarargsContext extends ParserRuleContext {
		public TerminalNode STAR() { return getToken(PythonParser.STAR, 0); }
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public VarargsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varargs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterVarargs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitVarargs(this);
		}
	}

	public final VarargsContext varargs() throws RecognitionException {
		VarargsContext _localctx = new VarargsContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_varargs);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(656);
			match(STAR);
			setState(657);
			name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VarkwargsContext extends ParserRuleContext {
		public TerminalNode POWER() { return getToken(PythonParser.POWER, 0); }
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public VarkwargsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varkwargs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterVarkwargs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitVarkwargs(this);
		}
	}

	public final VarkwargsContext varkwargs() throws RecognitionException {
		VarkwargsContext _localctx = new VarkwargsContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_varkwargs);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(659);
			match(POWER);
			setState(660);
			name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Logical_testContext extends ParserRuleContext {
		public Token op;
		public ComparisonContext comparison() {
			return getRuleContext(ComparisonContext.class,0);
		}
		public TerminalNode NOT() { return getToken(PythonParser.NOT, 0); }
		public List<Logical_testContext> logical_test() {
			return getRuleContexts(Logical_testContext.class);
		}
		public Logical_testContext logical_test(int i) {
			return getRuleContext(Logical_testContext.class,i);
		}
		public TerminalNode AND() { return getToken(PythonParser.AND, 0); }
		public TerminalNode OR() { return getToken(PythonParser.OR, 0); }
		public Logical_testContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logical_test; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterLogical_test(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitLogical_test(this);
		}
	}

	public final Logical_testContext logical_test() throws RecognitionException {
		return logical_test(0);
	}

	private Logical_testContext logical_test(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Logical_testContext _localctx = new Logical_testContext(_ctx, _parentState);
		Logical_testContext _prevctx = _localctx;
		int _startState = 74;
		enterRecursionRule(_localctx, 74, RULE_logical_test, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(666);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NONE:
			case AWAIT:
			case PRINT:
			case EXEC:
			case TRUE:
			case FALSE:
			case ELLIPSIS:
			case REVERSE_QUOTE:
			case ADD:
			case MINUS:
			case NOT_OP:
			case STRING:
			case DECIMAL_INTEGER:
			case OCT_INTEGER:
			case HEX_INTEGER:
			case BIN_INTEGER:
			case IMAG_NUMBER:
			case FLOAT_NUMBER:
			case OPEN_PAREN:
			case OPEN_BRACE:
			case OPEN_BRACKET:
			case NAME:
				{
				setState(663);
				comparison(0);
				}
				break;
			case NOT:
				{
				setState(664);
				match(NOT);
				setState(665);
				logical_test(3);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(676);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,98,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(674);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
					case 1:
						{
						_localctx = new Logical_testContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_logical_test);
						setState(668);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(669);
						((Logical_testContext)_localctx).op = match(AND);
						setState(670);
						logical_test(3);
						}
						break;
					case 2:
						{
						_localctx = new Logical_testContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_logical_test);
						setState(671);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(672);
						((Logical_testContext)_localctx).op = match(OR);
						setState(673);
						logical_test(2);
						}
						break;
					}
					} 
				}
				setState(678);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,98,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ComparisonContext extends ParserRuleContext {
		public Token optional;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<ComparisonContext> comparison() {
			return getRuleContexts(ComparisonContext.class);
		}
		public ComparisonContext comparison(int i) {
			return getRuleContext(ComparisonContext.class,i);
		}
		public TerminalNode LESS_THAN() { return getToken(PythonParser.LESS_THAN, 0); }
		public TerminalNode GREATER_THAN() { return getToken(PythonParser.GREATER_THAN, 0); }
		public TerminalNode EQUALS() { return getToken(PythonParser.EQUALS, 0); }
		public TerminalNode GT_EQ() { return getToken(PythonParser.GT_EQ, 0); }
		public TerminalNode LT_EQ() { return getToken(PythonParser.LT_EQ, 0); }
		public TerminalNode NOT_EQ_1() { return getToken(PythonParser.NOT_EQ_1, 0); }
		public TerminalNode NOT_EQ_2() { return getToken(PythonParser.NOT_EQ_2, 0); }
		public TerminalNode IN() { return getToken(PythonParser.IN, 0); }
		public TerminalNode IS() { return getToken(PythonParser.IS, 0); }
		public TerminalNode NOT() { return getToken(PythonParser.NOT, 0); }
		public ComparisonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterComparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitComparison(this);
		}
	}

	public final ComparisonContext comparison() throws RecognitionException {
		return comparison(0);
	}

	private ComparisonContext comparison(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ComparisonContext _localctx = new ComparisonContext(_ctx, _parentState);
		ComparisonContext _prevctx = _localctx;
		int _startState = 76;
		enterRecursionRule(_localctx, 76, RULE_comparison, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(680);
			expr(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(703);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,102,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ComparisonContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_comparison);
					setState(682);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(698);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case LESS_THAN:
						{
						setState(683);
						match(LESS_THAN);
						}
						break;
					case GREATER_THAN:
						{
						setState(684);
						match(GREATER_THAN);
						}
						break;
					case EQUALS:
						{
						setState(685);
						match(EQUALS);
						}
						break;
					case GT_EQ:
						{
						setState(686);
						match(GT_EQ);
						}
						break;
					case LT_EQ:
						{
						setState(687);
						match(LT_EQ);
						}
						break;
					case NOT_EQ_1:
						{
						setState(688);
						match(NOT_EQ_1);
						}
						break;
					case NOT_EQ_2:
						{
						setState(689);
						match(NOT_EQ_2);
						}
						break;
					case IN:
					case NOT:
						{
						setState(691);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(690);
							((ComparisonContext)_localctx).optional = match(NOT);
							}
						}

						setState(693);
						match(IN);
						}
						break;
					case IS:
						{
						setState(694);
						match(IS);
						setState(696);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(695);
							((ComparisonContext)_localctx).optional = match(NOT);
							}
						}

						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(700);
					comparison(3);
					}
					} 
				}
				setState(705);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,102,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExprContext extends ParserRuleContext {
		public Token op;
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public TerminalNode AWAIT() { return getToken(PythonParser.AWAIT, 0); }
		public List<TrailerContext> trailer() {
			return getRuleContexts(TrailerContext.class);
		}
		public TrailerContext trailer(int i) {
			return getRuleContext(TrailerContext.class,i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode ADD() { return getToken(PythonParser.ADD, 0); }
		public TerminalNode MINUS() { return getToken(PythonParser.MINUS, 0); }
		public TerminalNode NOT_OP() { return getToken(PythonParser.NOT_OP, 0); }
		public TerminalNode POWER() { return getToken(PythonParser.POWER, 0); }
		public TerminalNode STAR() { return getToken(PythonParser.STAR, 0); }
		public TerminalNode DIV() { return getToken(PythonParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(PythonParser.MOD, 0); }
		public TerminalNode IDIV() { return getToken(PythonParser.IDIV, 0); }
		public TerminalNode AT() { return getToken(PythonParser.AT, 0); }
		public TerminalNode LEFT_SHIFT() { return getToken(PythonParser.LEFT_SHIFT, 0); }
		public TerminalNode RIGHT_SHIFT() { return getToken(PythonParser.RIGHT_SHIFT, 0); }
		public TerminalNode AND_OP() { return getToken(PythonParser.AND_OP, 0); }
		public TerminalNode XOR() { return getToken(PythonParser.XOR, 0); }
		public TerminalNode OR_OP() { return getToken(PythonParser.OR_OP, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitExpr(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 78;
		enterRecursionRule(_localctx, 78, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(719);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,105,_ctx) ) {
			case 1:
				{
				setState(708);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AWAIT) {
					{
					setState(707);
					match(AWAIT);
					}
				}

				setState(710);
				atom();
				setState(714);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,104,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(711);
						trailer();
						}
						} 
					}
					setState(716);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,104,_ctx);
				}
				}
				break;
			case 2:
				{
				setState(717);
				((ExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1261007895663738880L) != 0)) ) {
					((ExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(718);
				expr(7);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(744);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,107,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(742);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,106,_ctx) ) {
					case 1:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(721);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(722);
						((ExprContext)_localctx).op = match(POWER);
						setState(723);
						expr(8);
						}
						break;
					case 2:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(724);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(725);
						((ExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 44)) & ~0x3f) == 0 && ((1L << (_la - 44)) & 16834561L) != 0)) ) {
							((ExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(726);
						expr(7);
						}
						break;
					case 3:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(727);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(728);
						((ExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==ADD || _la==MINUS) ) {
							((ExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(729);
						expr(6);
						}
						break;
					case 4:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(730);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(731);
						((ExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==LEFT_SHIFT || _la==RIGHT_SHIFT) ) {
							((ExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(732);
						expr(5);
						}
						break;
					case 5:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(733);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(734);
						((ExprContext)_localctx).op = match(AND_OP);
						setState(735);
						expr(4);
						}
						break;
					case 6:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(736);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(737);
						((ExprContext)_localctx).op = match(XOR);
						setState(738);
						expr(3);
						}
						break;
					case 7:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(739);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(740);
						((ExprContext)_localctx).op = match(OR_OP);
						setState(741);
						expr(2);
						}
						break;
					}
					} 
				}
				setState(746);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,107,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AtomContext extends ParserRuleContext {
		public TerminalNode OPEN_PAREN() { return getToken(PythonParser.OPEN_PAREN, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(PythonParser.CLOSE_PAREN, 0); }
		public Yield_exprContext yield_expr() {
			return getRuleContext(Yield_exprContext.class,0);
		}
		public Testlist_compContext testlist_comp() {
			return getRuleContext(Testlist_compContext.class,0);
		}
		public TerminalNode OPEN_BRACKET() { return getToken(PythonParser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(PythonParser.CLOSE_BRACKET, 0); }
		public TerminalNode OPEN_BRACE() { return getToken(PythonParser.OPEN_BRACE, 0); }
		public TerminalNode CLOSE_BRACE() { return getToken(PythonParser.CLOSE_BRACE, 0); }
		public DictorsetmakerContext dictorsetmaker() {
			return getRuleContext(DictorsetmakerContext.class,0);
		}
		public List<TerminalNode> REVERSE_QUOTE() { return getTokens(PythonParser.REVERSE_QUOTE); }
		public TerminalNode REVERSE_QUOTE(int i) {
			return getToken(PythonParser.REVERSE_QUOTE, i);
		}
		public TestlistContext testlist() {
			return getRuleContext(TestlistContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(PythonParser.COMMA, 0); }
		public TerminalNode ELLIPSIS() { return getToken(PythonParser.ELLIPSIS, 0); }
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode PRINT() { return getToken(PythonParser.PRINT, 0); }
		public TerminalNode EXEC() { return getToken(PythonParser.EXEC, 0); }
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode MINUS() { return getToken(PythonParser.MINUS, 0); }
		public TerminalNode NONE() { return getToken(PythonParser.NONE, 0); }
		public List<TerminalNode> STRING() { return getTokens(PythonParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(PythonParser.STRING, i);
		}
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitAtom(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_atom);
		int _la;
		try {
			int _alt;
			setState(784);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OPEN_PAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(747);
				match(OPEN_PAREN);
				setState(750);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case YIELD:
					{
					setState(748);
					yield_expr();
					}
					break;
				case NONE:
				case LAMBDA:
				case NOT:
				case AWAIT:
				case PRINT:
				case EXEC:
				case TRUE:
				case FALSE:
				case ELLIPSIS:
				case REVERSE_QUOTE:
				case STAR:
				case ADD:
				case MINUS:
				case NOT_OP:
				case STRING:
				case DECIMAL_INTEGER:
				case OCT_INTEGER:
				case HEX_INTEGER:
				case BIN_INTEGER:
				case IMAG_NUMBER:
				case FLOAT_NUMBER:
				case OPEN_PAREN:
				case OPEN_BRACE:
				case OPEN_BRACKET:
				case NAME:
					{
					setState(749);
					testlist_comp();
					}
					break;
				case CLOSE_PAREN:
					break;
				default:
					break;
				}
				setState(752);
				match(CLOSE_PAREN);
				}
				break;
			case OPEN_BRACKET:
				enterOuterAlt(_localctx, 2);
				{
				setState(753);
				match(OPEN_BRACKET);
				setState(755);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1261040812445138944L) != 0) || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 11007L) != 0)) {
					{
					setState(754);
					testlist_comp();
					}
				}

				setState(757);
				match(CLOSE_BRACKET);
				}
				break;
			case OPEN_BRACE:
				enterOuterAlt(_localctx, 3);
				{
				setState(758);
				match(OPEN_BRACE);
				setState(760);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1261322287421849600L) != 0) || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 11007L) != 0)) {
					{
					setState(759);
					dictorsetmaker();
					}
				}

				setState(762);
				match(CLOSE_BRACE);
				}
				break;
			case REVERSE_QUOTE:
				enterOuterAlt(_localctx, 4);
				{
				setState(763);
				match(REVERSE_QUOTE);
				setState(764);
				testlist();
				setState(766);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(765);
					match(COMMA);
					}
				}

				setState(768);
				match(REVERSE_QUOTE);
				}
				break;
			case ELLIPSIS:
				enterOuterAlt(_localctx, 5);
				{
				setState(770);
				match(ELLIPSIS);
				}
				break;
			case TRUE:
			case FALSE:
			case NAME:
				enterOuterAlt(_localctx, 6);
				{
				setState(771);
				name();
				}
				break;
			case PRINT:
				enterOuterAlt(_localctx, 7);
				{
				setState(772);
				match(PRINT);
				}
				break;
			case EXEC:
				enterOuterAlt(_localctx, 8);
				{
				setState(773);
				match(EXEC);
				}
				break;
			case MINUS:
			case DECIMAL_INTEGER:
			case OCT_INTEGER:
			case HEX_INTEGER:
			case BIN_INTEGER:
			case IMAG_NUMBER:
			case FLOAT_NUMBER:
				enterOuterAlt(_localctx, 9);
				{
				setState(775);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MINUS) {
					{
					setState(774);
					match(MINUS);
					}
				}

				setState(777);
				number();
				}
				break;
			case NONE:
				enterOuterAlt(_localctx, 10);
				{
				setState(778);
				match(NONE);
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 11);
				{
				setState(780); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(779);
						match(STRING);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(782); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,113,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DictorsetmakerContext extends ParserRuleContext {
		public List<TestContext> test() {
			return getRuleContexts(TestContext.class);
		}
		public TestContext test(int i) {
			return getRuleContext(TestContext.class,i);
		}
		public List<TerminalNode> COLON() { return getTokens(PythonParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(PythonParser.COLON, i);
		}
		public List<TerminalNode> POWER() { return getTokens(PythonParser.POWER); }
		public TerminalNode POWER(int i) {
			return getToken(PythonParser.POWER, i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public Comp_forContext comp_for() {
			return getRuleContext(Comp_forContext.class,0);
		}
		public Testlist_compContext testlist_comp() {
			return getRuleContext(Testlist_compContext.class,0);
		}
		public DictorsetmakerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dictorsetmaker; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterDictorsetmaker(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitDictorsetmaker(this);
		}
	}

	public final DictorsetmakerContext dictorsetmaker() throws RecognitionException {
		DictorsetmakerContext _localctx = new DictorsetmakerContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_dictorsetmaker);
		int _la;
		try {
			int _alt;
			setState(817);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,119,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(792);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NONE:
				case LAMBDA:
				case NOT:
				case AWAIT:
				case PRINT:
				case EXEC:
				case TRUE:
				case FALSE:
				case ELLIPSIS:
				case REVERSE_QUOTE:
				case ADD:
				case MINUS:
				case NOT_OP:
				case STRING:
				case DECIMAL_INTEGER:
				case OCT_INTEGER:
				case HEX_INTEGER:
				case BIN_INTEGER:
				case IMAG_NUMBER:
				case FLOAT_NUMBER:
				case OPEN_PAREN:
				case OPEN_BRACE:
				case OPEN_BRACKET:
				case NAME:
					{
					setState(786);
					test();
					setState(787);
					match(COLON);
					setState(788);
					test();
					}
					break;
				case POWER:
					{
					setState(790);
					match(POWER);
					setState(791);
					expr(0);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(805);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,117,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(794);
						match(COMMA);
						setState(801);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case NONE:
						case LAMBDA:
						case NOT:
						case AWAIT:
						case PRINT:
						case EXEC:
						case TRUE:
						case FALSE:
						case ELLIPSIS:
						case REVERSE_QUOTE:
						case ADD:
						case MINUS:
						case NOT_OP:
						case STRING:
						case DECIMAL_INTEGER:
						case OCT_INTEGER:
						case HEX_INTEGER:
						case BIN_INTEGER:
						case IMAG_NUMBER:
						case FLOAT_NUMBER:
						case OPEN_PAREN:
						case OPEN_BRACE:
						case OPEN_BRACKET:
						case NAME:
							{
							setState(795);
							test();
							setState(796);
							match(COLON);
							setState(797);
							test();
							}
							break;
						case POWER:
							{
							setState(799);
							match(POWER);
							setState(800);
							expr(0);
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						}
						} 
					}
					setState(807);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,117,_ctx);
				}
				setState(809);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(808);
					match(COMMA);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(811);
				test();
				setState(812);
				match(COLON);
				setState(813);
				test();
				setState(814);
				comp_for();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(816);
				testlist_comp();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Testlist_compContext extends ParserRuleContext {
		public List<TestContext> test() {
			return getRuleContexts(TestContext.class);
		}
		public TestContext test(int i) {
			return getRuleContext(TestContext.class,i);
		}
		public List<Star_exprContext> star_expr() {
			return getRuleContexts(Star_exprContext.class);
		}
		public Star_exprContext star_expr(int i) {
			return getRuleContext(Star_exprContext.class,i);
		}
		public Comp_forContext comp_for() {
			return getRuleContext(Comp_forContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public Testlist_compContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_testlist_comp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterTestlist_comp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitTestlist_comp(this);
		}
	}

	public final Testlist_compContext testlist_comp() throws RecognitionException {
		Testlist_compContext _localctx = new Testlist_compContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_testlist_comp);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(821);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NONE:
			case LAMBDA:
			case NOT:
			case AWAIT:
			case PRINT:
			case EXEC:
			case TRUE:
			case FALSE:
			case ELLIPSIS:
			case REVERSE_QUOTE:
			case ADD:
			case MINUS:
			case NOT_OP:
			case STRING:
			case DECIMAL_INTEGER:
			case OCT_INTEGER:
			case HEX_INTEGER:
			case BIN_INTEGER:
			case IMAG_NUMBER:
			case FLOAT_NUMBER:
			case OPEN_PAREN:
			case OPEN_BRACE:
			case OPEN_BRACKET:
			case NAME:
				{
				setState(819);
				test();
				}
				break;
			case STAR:
				{
				setState(820);
				star_expr();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(837);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FOR:
				{
				setState(823);
				comp_for();
				}
				break;
			case COMMA:
			case CLOSE_PAREN:
			case CLOSE_BRACE:
			case CLOSE_BRACKET:
				{
				setState(831);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,122,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(824);
						match(COMMA);
						setState(827);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case NONE:
						case LAMBDA:
						case NOT:
						case AWAIT:
						case PRINT:
						case EXEC:
						case TRUE:
						case FALSE:
						case ELLIPSIS:
						case REVERSE_QUOTE:
						case ADD:
						case MINUS:
						case NOT_OP:
						case STRING:
						case DECIMAL_INTEGER:
						case OCT_INTEGER:
						case HEX_INTEGER:
						case BIN_INTEGER:
						case IMAG_NUMBER:
						case FLOAT_NUMBER:
						case OPEN_PAREN:
						case OPEN_BRACE:
						case OPEN_BRACKET:
						case NAME:
							{
							setState(825);
							test();
							}
							break;
						case STAR:
							{
							setState(826);
							star_expr();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						}
						} 
					}
					setState(833);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,122,_ctx);
				}
				setState(835);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(834);
					match(COMMA);
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TestlistContext extends ParserRuleContext {
		public List<TestContext> test() {
			return getRuleContexts(TestContext.class);
		}
		public TestContext test(int i) {
			return getRuleContext(TestContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public TestlistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_testlist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterTestlist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitTestlist(this);
		}
	}

	public final TestlistContext testlist() throws RecognitionException {
		TestlistContext _localctx = new TestlistContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_testlist);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(839);
			test();
			setState(844);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,125,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(840);
					match(COMMA);
					setState(841);
					test();
					}
					} 
				}
				setState(846);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,125,_ctx);
			}
			setState(848);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,126,_ctx) ) {
			case 1:
				{
				setState(847);
				match(COMMA);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Dotted_nameContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public Dotted_nameContext dotted_name() {
			return getRuleContext(Dotted_nameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(PythonParser.DOT, 0); }
		public Dotted_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dotted_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterDotted_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitDotted_name(this);
		}
	}

	public final Dotted_nameContext dotted_name() throws RecognitionException {
		return dotted_name(0);
	}

	private Dotted_nameContext dotted_name(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Dotted_nameContext _localctx = new Dotted_nameContext(_ctx, _parentState);
		Dotted_nameContext _prevctx = _localctx;
		int _startState = 88;
		enterRecursionRule(_localctx, 88, RULE_dotted_name, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(851);
			name();
			}
			_ctx.stop = _input.LT(-1);
			setState(858);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,127,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Dotted_nameContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_dotted_name);
					setState(853);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(854);
					match(DOT);
					setState(855);
					name();
					}
					} 
				}
				setState(860);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,127,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(PythonParser.NAME, 0); }
		public TerminalNode TRUE() { return getToken(PythonParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(PythonParser.FALSE, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitName(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(861);
			_la = _input.LA(1);
			if ( !(((((_la - 39)) & ~0x3f) == 0 && ((1L << (_la - 39)) & 144115188075855875L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NumberContext extends ParserRuleContext {
		public IntegerContext integer() {
			return getRuleContext(IntegerContext.class,0);
		}
		public TerminalNode IMAG_NUMBER() { return getToken(PythonParser.IMAG_NUMBER, 0); }
		public TerminalNode FLOAT_NUMBER() { return getToken(PythonParser.FLOAT_NUMBER, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitNumber(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_number);
		try {
			setState(866);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DECIMAL_INTEGER:
			case OCT_INTEGER:
			case HEX_INTEGER:
			case BIN_INTEGER:
				enterOuterAlt(_localctx, 1);
				{
				setState(863);
				integer();
				}
				break;
			case IMAG_NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(864);
				match(IMAG_NUMBER);
				}
				break;
			case FLOAT_NUMBER:
				enterOuterAlt(_localctx, 3);
				{
				setState(865);
				match(FLOAT_NUMBER);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IntegerContext extends ParserRuleContext {
		public TerminalNode DECIMAL_INTEGER() { return getToken(PythonParser.DECIMAL_INTEGER, 0); }
		public TerminalNode OCT_INTEGER() { return getToken(PythonParser.OCT_INTEGER, 0); }
		public TerminalNode HEX_INTEGER() { return getToken(PythonParser.HEX_INTEGER, 0); }
		public TerminalNode BIN_INTEGER() { return getToken(PythonParser.BIN_INTEGER, 0); }
		public IntegerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_integer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterInteger(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitInteger(this);
		}
	}

	public final IntegerContext integer() throws RecognitionException {
		IntegerContext _localctx = new IntegerContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_integer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(868);
			_la = _input.LA(1);
			if ( !(((((_la - 84)) & ~0x3f) == 0 && ((1L << (_la - 84)) & 15L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Yield_exprContext extends ParserRuleContext {
		public TerminalNode YIELD() { return getToken(PythonParser.YIELD, 0); }
		public Yield_argContext yield_arg() {
			return getRuleContext(Yield_argContext.class,0);
		}
		public Yield_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_yield_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterYield_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitYield_expr(this);
		}
	}

	public final Yield_exprContext yield_expr() throws RecognitionException {
		Yield_exprContext _localctx = new Yield_exprContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_yield_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(870);
			match(YIELD);
			setState(872);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1261023220259094656L) != 0) || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 11007L) != 0)) {
				{
				setState(871);
				yield_arg();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Yield_argContext extends ParserRuleContext {
		public TerminalNode FROM() { return getToken(PythonParser.FROM, 0); }
		public TestContext test() {
			return getRuleContext(TestContext.class,0);
		}
		public TestlistContext testlist() {
			return getRuleContext(TestlistContext.class,0);
		}
		public Yield_argContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_yield_arg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterYield_arg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitYield_arg(this);
		}
	}

	public final Yield_argContext yield_arg() throws RecognitionException {
		Yield_argContext _localctx = new Yield_argContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_yield_arg);
		try {
			setState(877);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FROM:
				enterOuterAlt(_localctx, 1);
				{
				setState(874);
				match(FROM);
				setState(875);
				test();
				}
				break;
			case NONE:
			case LAMBDA:
			case NOT:
			case AWAIT:
			case PRINT:
			case EXEC:
			case TRUE:
			case FALSE:
			case ELLIPSIS:
			case REVERSE_QUOTE:
			case ADD:
			case MINUS:
			case NOT_OP:
			case STRING:
			case DECIMAL_INTEGER:
			case OCT_INTEGER:
			case HEX_INTEGER:
			case BIN_INTEGER:
			case IMAG_NUMBER:
			case FLOAT_NUMBER:
			case OPEN_PAREN:
			case OPEN_BRACE:
			case OPEN_BRACKET:
			case NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(876);
				testlist();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TrailerContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(PythonParser.DOT, 0); }
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public TrailerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_trailer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterTrailer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitTrailer(this);
		}
	}

	public final TrailerContext trailer() throws RecognitionException {
		TrailerContext _localctx = new TrailerContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_trailer);
		try {
			setState(885);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DOT:
				enterOuterAlt(_localctx, 1);
				{
				setState(879);
				match(DOT);
				setState(880);
				name();
				setState(882);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,131,_ctx) ) {
				case 1:
					{
					setState(881);
					arguments();
					}
					break;
				}
				}
				break;
			case OPEN_PAREN:
			case OPEN_BRACKET:
				enterOuterAlt(_localctx, 2);
				{
				setState(884);
				arguments();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentsContext extends ParserRuleContext {
		public TerminalNode OPEN_PAREN() { return getToken(PythonParser.OPEN_PAREN, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(PythonParser.CLOSE_PAREN, 0); }
		public ArglistContext arglist() {
			return getRuleContext(ArglistContext.class,0);
		}
		public TerminalNode OPEN_BRACKET() { return getToken(PythonParser.OPEN_BRACKET, 0); }
		public SubscriptlistContext subscriptlist() {
			return getRuleContext(SubscriptlistContext.class,0);
		}
		public TerminalNode CLOSE_BRACKET() { return getToken(PythonParser.CLOSE_BRACKET, 0); }
		public ArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitArguments(this);
		}
	}

	public final ArgumentsContext arguments() throws RecognitionException {
		ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_arguments);
		int _la;
		try {
			setState(896);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OPEN_PAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(887);
				match(OPEN_PAREN);
				setState(889);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1261322287421849600L) != 0) || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 11007L) != 0)) {
					{
					setState(888);
					arglist();
					}
				}

				setState(891);
				match(CLOSE_PAREN);
				}
				break;
			case OPEN_BRACKET:
				enterOuterAlt(_localctx, 2);
				{
				setState(892);
				match(OPEN_BRACKET);
				setState(893);
				subscriptlist();
				setState(894);
				match(CLOSE_BRACKET);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArglistContext extends ParserRuleContext {
		public List<ArgumentContext> argument() {
			return getRuleContexts(ArgumentContext.class);
		}
		public ArgumentContext argument(int i) {
			return getRuleContext(ArgumentContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public ArglistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arglist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterArglist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitArglist(this);
		}
	}

	public final ArglistContext arglist() throws RecognitionException {
		ArglistContext _localctx = new ArglistContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_arglist);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(898);
			argument();
			setState(903);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,135,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(899);
					match(COMMA);
					setState(900);
					argument();
					}
					} 
				}
				setState(905);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,135,_ctx);
			}
			setState(907);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(906);
				match(COMMA);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentContext extends ParserRuleContext {
		public List<TestContext> test() {
			return getRuleContexts(TestContext.class);
		}
		public TestContext test(int i) {
			return getRuleContext(TestContext.class,i);
		}
		public Comp_forContext comp_for() {
			return getRuleContext(Comp_forContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(PythonParser.ASSIGN, 0); }
		public TerminalNode POWER() { return getToken(PythonParser.POWER, 0); }
		public TerminalNode STAR() { return getToken(PythonParser.STAR, 0); }
		public ArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitArgument(this);
		}
	}

	public final ArgumentContext argument() throws RecognitionException {
		ArgumentContext _localctx = new ArgumentContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_argument);
		int _la;
		try {
			setState(917);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NONE:
			case LAMBDA:
			case NOT:
			case AWAIT:
			case PRINT:
			case EXEC:
			case TRUE:
			case FALSE:
			case ELLIPSIS:
			case REVERSE_QUOTE:
			case ADD:
			case MINUS:
			case NOT_OP:
			case STRING:
			case DECIMAL_INTEGER:
			case OCT_INTEGER:
			case HEX_INTEGER:
			case BIN_INTEGER:
			case IMAG_NUMBER:
			case FLOAT_NUMBER:
			case OPEN_PAREN:
			case OPEN_BRACE:
			case OPEN_BRACKET:
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(909);
				test();
				setState(913);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case FOR:
					{
					setState(910);
					comp_for();
					}
					break;
				case ASSIGN:
					{
					setState(911);
					match(ASSIGN);
					setState(912);
					test();
					}
					break;
				case COMMA:
				case CLOSE_PAREN:
					break;
				default:
					break;
				}
				}
				break;
			case STAR:
			case POWER:
				enterOuterAlt(_localctx, 2);
				{
				setState(915);
				_la = _input.LA(1);
				if ( !(_la==STAR || _la==POWER) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(916);
				test();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SubscriptlistContext extends ParserRuleContext {
		public List<SubscriptContext> subscript() {
			return getRuleContexts(SubscriptContext.class);
		}
		public SubscriptContext subscript(int i) {
			return getRuleContext(SubscriptContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PythonParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PythonParser.COMMA, i);
		}
		public SubscriptlistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subscriptlist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterSubscriptlist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitSubscriptlist(this);
		}
	}

	public final SubscriptlistContext subscriptlist() throws RecognitionException {
		SubscriptlistContext _localctx = new SubscriptlistContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_subscriptlist);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(919);
			subscript();
			setState(924);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,139,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(920);
					match(COMMA);
					setState(921);
					subscript();
					}
					} 
				}
				setState(926);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,139,_ctx);
			}
			setState(928);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(927);
				match(COMMA);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SubscriptContext extends ParserRuleContext {
		public TerminalNode ELLIPSIS() { return getToken(PythonParser.ELLIPSIS, 0); }
		public List<TestContext> test() {
			return getRuleContexts(TestContext.class);
		}
		public TestContext test(int i) {
			return getRuleContext(TestContext.class,i);
		}
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public SliceopContext sliceop() {
			return getRuleContext(SliceopContext.class,0);
		}
		public SubscriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subscript; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterSubscript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitSubscript(this);
		}
	}

	public final SubscriptContext subscript() throws RecognitionException {
		SubscriptContext _localctx = new SubscriptContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_subscript);
		int _la;
		try {
			setState(948);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,146,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(930);
				match(ELLIPSIS);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(931);
				test();
				setState(939);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
					{
					setState(932);
					match(COLON);
					setState(934);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1261023220259094528L) != 0) || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 11007L) != 0)) {
						{
						setState(933);
						test();
						}
					}

					setState(937);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COLON) {
						{
						setState(936);
						sliceop();
						}
					}

					}
				}

				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(941);
				match(COLON);
				setState(943);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1261023220259094528L) != 0) || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 11007L) != 0)) {
					{
					setState(942);
					test();
					}
				}

				setState(946);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
					{
					setState(945);
					sliceop();
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SliceopContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(PythonParser.COLON, 0); }
		public TestContext test() {
			return getRuleContext(TestContext.class,0);
		}
		public SliceopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sliceop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterSliceop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitSliceop(this);
		}
	}

	public final SliceopContext sliceop() throws RecognitionException {
		SliceopContext _localctx = new SliceopContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_sliceop);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(950);
			match(COLON);
			setState(952);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1261023220259094528L) != 0) || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 11007L) != 0)) {
				{
				setState(951);
				test();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comp_forContext extends ParserRuleContext {
		public TerminalNode FOR() { return getToken(PythonParser.FOR, 0); }
		public ExprlistContext exprlist() {
			return getRuleContext(ExprlistContext.class,0);
		}
		public TerminalNode IN() { return getToken(PythonParser.IN, 0); }
		public Logical_testContext logical_test() {
			return getRuleContext(Logical_testContext.class,0);
		}
		public Comp_iterContext comp_iter() {
			return getRuleContext(Comp_iterContext.class,0);
		}
		public Comp_forContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comp_for; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterComp_for(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitComp_for(this);
		}
	}

	public final Comp_forContext comp_for() throws RecognitionException {
		Comp_forContext _localctx = new Comp_forContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_comp_for);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(954);
			match(FOR);
			setState(955);
			exprlist();
			setState(956);
			match(IN);
			setState(957);
			logical_test(0);
			setState(959);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IF || _la==FOR) {
				{
				setState(958);
				comp_iter();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comp_iterContext extends ParserRuleContext {
		public Comp_forContext comp_for() {
			return getRuleContext(Comp_forContext.class,0);
		}
		public TerminalNode IF() { return getToken(PythonParser.IF, 0); }
		public TestContext test() {
			return getRuleContext(TestContext.class,0);
		}
		public Comp_iterContext comp_iter() {
			return getRuleContext(Comp_iterContext.class,0);
		}
		public Comp_iterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comp_iter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).enterComp_iter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PythonParserListener ) ((PythonParserListener)listener).exitComp_iter(this);
		}
	}

	public final Comp_iterContext comp_iter() throws RecognitionException {
		Comp_iterContext _localctx = new Comp_iterContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_comp_iter);
		int _la;
		try {
			setState(967);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FOR:
				enterOuterAlt(_localctx, 1);
				{
				setState(961);
				comp_for();
				}
				break;
			case IF:
				enterOuterAlt(_localctx, 2);
				{
				setState(962);
				match(IF);
				setState(963);
				test();
				setState(965);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IF || _la==FOR) {
					{
					setState(964);
					comp_iter();
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 12:
			return except_clause_sempred((Except_clauseContext)_localctx, predIndex);
		case 22:
			return small_stmt_sempred((Small_stmtContext)_localctx, predIndex);
		case 25:
			return assign_part_sempred((Assign_partContext)_localctx, predIndex);
		case 37:
			return logical_test_sempred((Logical_testContext)_localctx, predIndex);
		case 38:
			return comparison_sempred((ComparisonContext)_localctx, predIndex);
		case 39:
			return expr_sempred((ExprContext)_localctx, predIndex);
		case 44:
			return dotted_name_sempred((Dotted_nameContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean except_clause_sempred(Except_clauseContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return this.CheckVersion(2);
		case 1:
			return this.CheckVersion(3);
		}
		return true;
	}
	private boolean small_stmt_sempred(Small_stmtContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return this.CheckVersion(2);
		case 3:
			return this.CheckVersion(2);
		case 4:
			return this.CheckVersion(3);
		}
		return true;
	}
	private boolean assign_part_sempred(Assign_partContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return this.CheckVersion(3);
		}
		return true;
	}
	private boolean logical_test_sempred(Logical_testContext _localctx, int predIndex) {
		switch (predIndex) {
		case 6:
			return precpred(_ctx, 2);
		case 7:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean comparison_sempred(ComparisonContext _localctx, int predIndex) {
		switch (predIndex) {
		case 8:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 9:
			return precpred(_ctx, 8);
		case 10:
			return precpred(_ctx, 6);
		case 11:
			return precpred(_ctx, 5);
		case 12:
			return precpred(_ctx, 4);
		case 13:
			return precpred(_ctx, 3);
		case 14:
			return precpred(_ctx, 2);
		case 15:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean dotted_name_sempred(Dotted_nameContext _localctx, int predIndex) {
		switch (predIndex) {
		case 16:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001d\u03ca\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002"+
		"-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u0002"+
		"2\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u0002"+
		"7\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0003\u0000z\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u0083\b\u0001"+
		"\u0001\u0002\u0001\u0002\u0004\u0002\u0087\b\u0002\u000b\u0002\f\u0002"+
		"\u0088\u0001\u0003\u0001\u0003\u0005\u0003\u008d\b\u0003\n\u0003\f\u0003"+
		"\u0090\t\u0003\u0001\u0004\u0001\u0004\u0003\u0004\u0094\b\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005\u009b"+
		"\b\u0005\n\u0005\f\u0005\u009e\t\u0005\u0001\u0005\u0003\u0005\u00a1\b"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003"+
		"\u0005\u00a8\b\u0005\u0001\u0005\u0003\u0005\u00ab\b\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0003\u0005\u00b4\b\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0004\u0005\u00ba\b\u0005\u000b\u0005\f\u0005\u00bb\u0001\u0005\u0003"+
		"\u0005\u00bf\b\u0005\u0001\u0005\u0003\u0005\u00c2\b\u0005\u0001\u0005"+
		"\u0003\u0005\u00c5\b\u0005\u0001\u0005\u0003\u0005\u00c8\b\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005\u00ce\b\u0005\n"+
		"\u0005\f\u0005\u00d1\t\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0005\u0005\u00d7\b\u0005\n\u0005\f\u0005\u00da\t\u0005\u0001\u0005"+
		"\u0001\u0005\u0003\u0005\u00de\b\u0005\u0003\u0005\u00e0\b\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0004\u0006\u00e6\b\u0006\u000b"+
		"\u0006\f\u0006\u00e7\u0001\u0006\u0001\u0006\u0003\u0006\u00ec\b\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u00f2\b\u0007"+
		"\u0001\u0007\u0003\u0007\u00f5\b\u0007\u0001\u0007\u0001\u0007\u0001\b"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b"+
		"\u0109\b\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003\f\u0117\b\f\u0003\f\u0119"+
		"\b\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u0122"+
		"\b\r\u0001\r\u0003\r\u0125\b\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0003"+
		"\u000e\u012b\b\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003"+
		"\u000e\u0131\b\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u0136"+
		"\b\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0003\u000f\u013e\b\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003"+
		"\u000f\u0143\b\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u0147\b\u000f"+
		"\u0001\u000f\u0003\u000f\u014a\b\u000f\u0001\u000f\u0003\u000f\u014d\b"+
		"\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u0151\b\u000f\u0003\u000f\u0153"+
		"\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u015e\b\u0012\n"+
		"\u0012\f\u0012\u0161\t\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0003"+
		"\u0013\u0166\b\u0013\u0001\u0013\u0003\u0013\u0169\b\u0013\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0003\u0014\u016e\b\u0014\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0005\u0015\u0173\b\u0015\n\u0015\f\u0015\u0176\t\u0015\u0001"+
		"\u0015\u0003\u0015\u0179\b\u0015\u0001\u0015\u0001\u0015\u0001\u0016\u0001"+
		"\u0016\u0003\u0016\u017f\b\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0005\u0016\u0186\b\u0016\n\u0016\f\u0016\u0189\t\u0016"+
		"\u0001\u0016\u0003\u0016\u018c\b\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0004\u0016\u0192\b\u0016\u000b\u0016\f\u0016\u0193\u0001"+
		"\u0016\u0003\u0016\u0197\b\u0016\u0003\u0016\u0199\b\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0003\u0016\u01a4\b\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u01ac\b\u0016"+
		"\u0003\u0016\u01ae\b\u0016\u0003\u0016\u01b0\b\u0016\u0001\u0016\u0001"+
		"\u0016\u0003\u0016\u01b4\b\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0005\u0016\u01bb\b\u0016\n\u0016\f\u0016\u01be\t\u0016"+
		"\u0001\u0016\u0001\u0016\u0004\u0016\u01c2\b\u0016\u000b\u0016\f\u0016"+
		"\u01c3\u0003\u0016\u01c6\b\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u01cf\b\u0016\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0005\u0016\u01d5\b\u0016\n"+
		"\u0016\f\u0016\u01d8\t\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u01e1\b\u0016\u0003"+
		"\u0016\u01e3\b\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0003\u0016\u01eb\b\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0005\u0016\u01f2\b\u0016\n\u0016\f\u0016"+
		"\u01f5\t\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u01f9\b\u0016\u0001"+
		"\u0017\u0001\u0017\u0003\u0017\u01fd\b\u0017\u0001\u0017\u0001\u0017\u0004"+
		"\u0017\u0201\b\u0017\u000b\u0017\f\u0017\u0202\u0001\u0017\u0001\u0017"+
		"\u0003\u0017\u0207\b\u0017\u0001\u0017\u0003\u0017\u020a\b\u0017\u0001"+
		"\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0005\u0019\u0213\b\u0019\n\u0019\f\u0019\u0216\t\u0019\u0001\u0019"+
		"\u0001\u0019\u0003\u0019\u021a\b\u0019\u0001\u0019\u0003\u0019\u021d\b"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0003"+
		"\u0019\u0224\b\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0003\u0019\u022b\b\u0019\u0003\u0019\u022d\b\u0019\u0001\u001a"+
		"\u0001\u001a\u0001\u001a\u0005\u001a\u0232\b\u001a\n\u001a\f\u001a\u0235"+
		"\t\u001a\u0001\u001a\u0003\u001a\u0238\b\u001a\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0005\u001b\u023d\b\u001b\n\u001b\f\u001b\u0240\t\u001b\u0001"+
		"\u001b\u0003\u001b\u0243\b\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0003"+
		"\u001c\u0248\b\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0005\u001d\u024d"+
		"\b\u001d\n\u001d\f\u001d\u0250\t\u001d\u0001\u001e\u0001\u001e\u0001\u001e"+
		"\u0003\u001e\u0255\b\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f"+
		"\u0001\u001f\u0001\u001f\u0003\u001f\u025d\b\u001f\u0001\u001f\u0001\u001f"+
		"\u0003\u001f\u0261\b\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u0265\b"+
		"\u001f\u0001 \u0001 \u0001 \u0003 \u026a\b \u0001 \u0001 \u0001 \u0003"+
		" \u026f\b \u0001 \u0001 \u0003 \u0273\b \u0001 \u0003 \u0276\b \u0001"+
		" \u0003 \u0279\b \u0001 \u0001 \u0003 \u027d\b \u0003 \u027f\b \u0001"+
		"!\u0001!\u0001!\u0005!\u0284\b!\n!\f!\u0287\t!\u0001\"\u0001\"\u0001\""+
		"\u0003\"\u028c\b\"\u0001\"\u0003\"\u028f\b\"\u0001#\u0001#\u0001#\u0001"+
		"$\u0001$\u0001$\u0001%\u0001%\u0001%\u0001%\u0003%\u029b\b%\u0001%\u0001"+
		"%\u0001%\u0001%\u0001%\u0001%\u0005%\u02a3\b%\n%\f%\u02a6\t%\u0001&\u0001"+
		"&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001"+
		"&\u0003&\u02b4\b&\u0001&\u0001&\u0001&\u0003&\u02b9\b&\u0003&\u02bb\b"+
		"&\u0001&\u0005&\u02be\b&\n&\f&\u02c1\t&\u0001\'\u0001\'\u0003\'\u02c5"+
		"\b\'\u0001\'\u0001\'\u0005\'\u02c9\b\'\n\'\f\'\u02cc\t\'\u0001\'\u0001"+
		"\'\u0003\'\u02d0\b\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001"+
		"\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001"+
		"\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0005\'\u02e7\b\'\n\'\f\'\u02ea"+
		"\t\'\u0001(\u0001(\u0001(\u0003(\u02ef\b(\u0001(\u0001(\u0001(\u0003("+
		"\u02f4\b(\u0001(\u0001(\u0001(\u0003(\u02f9\b(\u0001(\u0001(\u0001(\u0001"+
		"(\u0003(\u02ff\b(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0003"+
		"(\u0308\b(\u0001(\u0001(\u0001(\u0004(\u030d\b(\u000b(\f(\u030e\u0003"+
		"(\u0311\b(\u0001)\u0001)\u0001)\u0001)\u0001)\u0001)\u0003)\u0319\b)\u0001"+
		")\u0001)\u0001)\u0001)\u0001)\u0001)\u0001)\u0003)\u0322\b)\u0005)\u0324"+
		"\b)\n)\f)\u0327\t)\u0001)\u0003)\u032a\b)\u0001)\u0001)\u0001)\u0001)"+
		"\u0001)\u0001)\u0003)\u0332\b)\u0001*\u0001*\u0003*\u0336\b*\u0001*\u0001"+
		"*\u0001*\u0001*\u0003*\u033c\b*\u0005*\u033e\b*\n*\f*\u0341\t*\u0001*"+
		"\u0003*\u0344\b*\u0003*\u0346\b*\u0001+\u0001+\u0001+\u0005+\u034b\b+"+
		"\n+\f+\u034e\t+\u0001+\u0003+\u0351\b+\u0001,\u0001,\u0001,\u0001,\u0001"+
		",\u0001,\u0005,\u0359\b,\n,\f,\u035c\t,\u0001-\u0001-\u0001.\u0001.\u0001"+
		".\u0003.\u0363\b.\u0001/\u0001/\u00010\u00010\u00030\u0369\b0\u00011\u0001"+
		"1\u00011\u00031\u036e\b1\u00012\u00012\u00012\u00032\u0373\b2\u00012\u0003"+
		"2\u0376\b2\u00013\u00013\u00033\u037a\b3\u00013\u00013\u00013\u00013\u0001"+
		"3\u00033\u0381\b3\u00014\u00014\u00014\u00054\u0386\b4\n4\f4\u0389\t4"+
		"\u00014\u00034\u038c\b4\u00015\u00015\u00015\u00015\u00035\u0392\b5\u0001"+
		"5\u00015\u00035\u0396\b5\u00016\u00016\u00016\u00056\u039b\b6\n6\f6\u039e"+
		"\t6\u00016\u00036\u03a1\b6\u00017\u00017\u00017\u00017\u00037\u03a7\b"+
		"7\u00017\u00037\u03aa\b7\u00037\u03ac\b7\u00017\u00017\u00037\u03b0\b"+
		"7\u00017\u00037\u03b3\b7\u00037\u03b5\b7\u00018\u00018\u00038\u03b9\b"+
		"8\u00019\u00019\u00019\u00019\u00019\u00039\u03c0\b9\u0001:\u0001:\u0001"+
		":\u0001:\u0003:\u03c6\b:\u0003:\u03c8\b:\u0001:\u0000\u0004JLNX;\u0000"+
		"\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c"+
		"\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprt\u0000\n\u0001\u0001"+
		"\u0003\u0003\u0001\u0000)*\u0001\u0000FR\u0002\u000078<<\u0003\u0000,"+
		",9;DD\u0001\u000078\u0001\u000056\u0002\u0000\'(``\u0001\u0000TW\u0002"+
		"\u0000,,00\u0457\u0000y\u0001\u0000\u0000\u0000\u0002\u0082\u0001\u0000"+
		"\u0000\u0000\u0004\u0086\u0001\u0000\u0000\u0000\u0006\u008a\u0001\u0000"+
		"\u0000\u0000\b\u0093\u0001\u0000\u0000\u0000\n\u00df\u0001\u0000\u0000"+
		"\u0000\f\u00eb\u0001\u0000\u0000\u0000\u000e\u00ed\u0001\u0000\u0000\u0000"+
		"\u0010\u00f8\u0001\u0000\u0000\u0000\u0012\u00fd\u0001\u0000\u0000\u0000"+
		"\u0014\u0101\u0001\u0000\u0000\u0000\u0016\u0105\u0001\u0000\u0000\u0000"+
		"\u0018\u010a\u0001\u0000\u0000\u0000\u001a\u011d\u0001\u0000\u0000\u0000"+
		"\u001c\u012a\u0001\u0000\u0000\u0000\u001e\u0152\u0001\u0000\u0000\u0000"+
		" \u0154\u0001\u0000\u0000\u0000\"\u0157\u0001\u0000\u0000\u0000$\u015a"+
		"\u0001\u0000\u0000\u0000&\u0168\u0001\u0000\u0000\u0000(\u016a\u0001\u0000"+
		"\u0000\u0000*\u016f\u0001\u0000\u0000\u0000,\u01f8\u0001\u0000\u0000\u0000"+
		".\u0209\u0001\u0000\u0000\u00000\u020b\u0001\u0000\u0000\u00002\u022c"+
		"\u0001\u0000\u0000\u00004\u022e\u0001\u0000\u0000\u00006\u0239\u0001\u0000"+
		"\u0000\u00008\u0244\u0001\u0000\u0000\u0000:\u0249\u0001\u0000\u0000\u0000"+
		"<\u0251\u0001\u0000\u0000\u0000>\u0264\u0001\u0000\u0000\u0000@\u027e"+
		"\u0001\u0000\u0000\u0000B\u0280\u0001\u0000\u0000\u0000D\u028e\u0001\u0000"+
		"\u0000\u0000F\u0290\u0001\u0000\u0000\u0000H\u0293\u0001\u0000\u0000\u0000"+
		"J\u029a\u0001\u0000\u0000\u0000L\u02a7\u0001\u0000\u0000\u0000N\u02cf"+
		"\u0001\u0000\u0000\u0000P\u0310\u0001\u0000\u0000\u0000R\u0331\u0001\u0000"+
		"\u0000\u0000T\u0335\u0001\u0000\u0000\u0000V\u0347\u0001\u0000\u0000\u0000"+
		"X\u0352\u0001\u0000\u0000\u0000Z\u035d\u0001\u0000\u0000\u0000\\\u0362"+
		"\u0001\u0000\u0000\u0000^\u0364\u0001\u0000\u0000\u0000`\u0366\u0001\u0000"+
		"\u0000\u0000b\u036d\u0001\u0000\u0000\u0000d\u0375\u0001\u0000\u0000\u0000"+
		"f\u0380\u0001\u0000\u0000\u0000h\u0382\u0001\u0000\u0000\u0000j\u0395"+
		"\u0001\u0000\u0000\u0000l\u0397\u0001\u0000\u0000\u0000n\u03b4\u0001\u0000"+
		"\u0000\u0000p\u03b6\u0001\u0000\u0000\u0000r\u03ba\u0001\u0000\u0000\u0000"+
		"t\u03c7\u0001\u0000\u0000\u0000vz\u0003\u0002\u0001\u0000wz\u0003\u0004"+
		"\u0002\u0000xz\u0003\u0006\u0003\u0000yv\u0001\u0000\u0000\u0000yw\u0001"+
		"\u0000\u0000\u0000yx\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000"+
		"z{\u0001\u0000\u0000\u0000{|\u0005\u0000\u0000\u0001|\u0001\u0001\u0000"+
		"\u0000\u0000}\u0083\u0005\u0003\u0000\u0000~\u0083\u0003*\u0015\u0000"+
		"\u007f\u0080\u0003\n\u0005\u0000\u0080\u0081\u0005\u0003\u0000\u0000\u0081"+
		"\u0083\u0001\u0000\u0000\u0000\u0082}\u0001\u0000\u0000\u0000\u0082~\u0001"+
		"\u0000\u0000\u0000\u0082\u007f\u0001\u0000\u0000\u0000\u0083\u0003\u0001"+
		"\u0000\u0000\u0000\u0084\u0087\u0005\u0003\u0000\u0000\u0085\u0087\u0003"+
		"\b\u0004\u0000\u0086\u0084\u0001\u0000\u0000\u0000\u0086\u0085\u0001\u0000"+
		"\u0000\u0000\u0087\u0088\u0001\u0000\u0000\u0000\u0088\u0086\u0001\u0000"+
		"\u0000\u0000\u0088\u0089\u0001\u0000\u0000\u0000\u0089\u0005\u0001\u0000"+
		"\u0000\u0000\u008a\u008e\u0003V+\u0000\u008b\u008d\u0005\u0003\u0000\u0000"+
		"\u008c\u008b\u0001\u0000\u0000\u0000\u008d\u0090\u0001\u0000\u0000\u0000"+
		"\u008e\u008c\u0001\u0000\u0000\u0000\u008e\u008f\u0001\u0000\u0000\u0000"+
		"\u008f\u0007\u0001\u0000\u0000\u0000\u0090\u008e\u0001\u0000\u0000\u0000"+
		"\u0091\u0094\u0003*\u0015\u0000\u0092\u0094\u0003\n\u0005\u0000\u0093"+
		"\u0091\u0001\u0000\u0000\u0000\u0093\u0092\u0001\u0000\u0000\u0000\u0094"+
		"\t\u0001\u0000\u0000\u0000\u0095\u0096\u0005\r\u0000\u0000\u0096\u0097"+
		"\u0003>\u001f\u0000\u0097\u0098\u0005.\u0000\u0000\u0098\u009c\u0003\f"+
		"\u0006\u0000\u0099\u009b\u0003\u0010\b\u0000\u009a\u0099\u0001\u0000\u0000"+
		"\u0000\u009b\u009e\u0001\u0000\u0000\u0000\u009c\u009a\u0001\u0000\u0000"+
		"\u0000\u009c\u009d\u0001\u0000\u0000\u0000\u009d\u00a0\u0001\u0000\u0000"+
		"\u0000\u009e\u009c\u0001\u0000\u0000\u0000\u009f\u00a1\u0003\u0012\t\u0000"+
		"\u00a0\u009f\u0001\u0000\u0000\u0000\u00a0\u00a1\u0001\u0000\u0000\u0000"+
		"\u00a1\u00e0\u0001\u0000\u0000\u0000\u00a2\u00a3\u0005\u0010\u0000\u0000"+
		"\u00a3\u00a4\u0003>\u001f\u0000\u00a4\u00a5\u0005.\u0000\u0000\u00a5\u00a7"+
		"\u0003\f\u0006\u0000\u00a6\u00a8\u0003\u0012\t\u0000\u00a7\u00a6\u0001"+
		"\u0000\u0000\u0000\u00a7\u00a8\u0001\u0000\u0000\u0000\u00a8\u00e0\u0001"+
		"\u0000\u0000\u0000\u00a9\u00ab\u0005#\u0000\u0000\u00aa\u00a9\u0001\u0000"+
		"\u0000\u0000\u00aa\u00ab\u0001\u0000\u0000\u0000\u00ab\u00ac\u0001\u0000"+
		"\u0000\u0000\u00ac\u00ad\u0005\u0011\u0000\u0000\u00ad\u00ae\u00034\u001a"+
		"\u0000\u00ae\u00af\u0005\u0012\u0000\u0000\u00af\u00b0\u0003V+\u0000\u00b0"+
		"\u00b1\u0005.\u0000\u0000\u00b1\u00b3\u0003\f\u0006\u0000\u00b2\u00b4"+
		"\u0003\u0012\t\u0000\u00b3\u00b2\u0001\u0000\u0000\u0000\u00b3\u00b4\u0001"+
		"\u0000\u0000\u0000\u00b4\u00e0\u0001\u0000\u0000\u0000\u00b5\u00b6\u0005"+
		"\u0013\u0000\u0000\u00b6\u00b7\u0005.\u0000\u0000\u00b7\u00c4\u0003\f"+
		"\u0006\u0000\u00b8\u00ba\u0003\u0018\f\u0000\u00b9\u00b8\u0001\u0000\u0000"+
		"\u0000\u00ba\u00bb\u0001\u0000\u0000\u0000\u00bb\u00b9\u0001\u0000\u0000"+
		"\u0000\u00bb\u00bc\u0001\u0000\u0000\u0000\u00bc\u00be\u0001\u0000\u0000"+
		"\u0000\u00bd\u00bf\u0003\u0012\t\u0000\u00be\u00bd\u0001\u0000\u0000\u0000"+
		"\u00be\u00bf\u0001\u0000\u0000\u0000\u00bf\u00c1\u0001\u0000\u0000\u0000"+
		"\u00c0\u00c2\u0003\u0014\n\u0000\u00c1\u00c0\u0001\u0000\u0000\u0000\u00c1"+
		"\u00c2\u0001\u0000\u0000\u0000\u00c2\u00c5\u0001\u0000\u0000\u0000\u00c3"+
		"\u00c5\u0003\u0014\n\u0000\u00c4\u00b9\u0001\u0000\u0000\u0000\u00c4\u00c3"+
		"\u0001\u0000\u0000\u0000\u00c5\u00e0\u0001\u0000\u0000\u0000\u00c6\u00c8"+
		"\u0005#\u0000\u0000\u00c7\u00c6\u0001\u0000\u0000\u0000\u00c7\u00c8\u0001"+
		"\u0000\u0000\u0000\u00c8\u00c9\u0001\u0000\u0000\u0000\u00c9\u00ca\u0005"+
		"\u0016\u0000\u0000\u00ca\u00cf\u0003\u0016\u000b\u0000\u00cb\u00cc\u0005"+
		"-\u0000\u0000\u00cc\u00ce\u0003\u0016\u000b\u0000\u00cd\u00cb\u0001\u0000"+
		"\u0000\u0000\u00ce\u00d1\u0001\u0000\u0000\u0000\u00cf\u00cd\u0001\u0000"+
		"\u0000\u0000\u00cf\u00d0\u0001\u0000\u0000\u0000\u00d0\u00d2\u0001\u0000"+
		"\u0000\u0000\u00d1\u00cf\u0001\u0000\u0000\u0000\u00d2\u00d3\u0005.\u0000"+
		"\u0000\u00d3\u00d4\u0003\f\u0006\u0000\u00d4\u00e0\u0001\u0000\u0000\u0000"+
		"\u00d5\u00d7\u0003\u000e\u0007\u0000\u00d6\u00d5\u0001\u0000\u0000\u0000"+
		"\u00d7\u00da\u0001\u0000\u0000\u0000\u00d8\u00d6\u0001\u0000\u0000\u0000"+
		"\u00d8\u00d9\u0001\u0000\u0000\u0000\u00d9\u00dd\u0001\u0000\u0000\u0000"+
		"\u00da\u00d8\u0001\u0000\u0000\u0000\u00db\u00de\u0003\u001a\r\u0000\u00dc"+
		"\u00de\u0003\u001c\u000e\u0000\u00dd\u00db\u0001\u0000\u0000\u0000\u00dd"+
		"\u00dc\u0001\u0000\u0000\u0000\u00de\u00e0\u0001\u0000\u0000\u0000\u00df"+
		"\u0095\u0001\u0000\u0000\u0000\u00df\u00a2\u0001\u0000\u0000\u0000\u00df"+
		"\u00aa\u0001\u0000\u0000\u0000\u00df\u00b5\u0001\u0000\u0000\u0000\u00df"+
		"\u00c7\u0001\u0000\u0000\u0000\u00df\u00d8\u0001\u0000\u0000\u0000\u00e0"+
		"\u000b\u0001\u0000\u0000\u0000\u00e1\u00ec\u0003*\u0015\u0000\u00e2\u00e3"+
		"\u0005\u0003\u0000\u0000\u00e3\u00e5\u0005\u0001\u0000\u0000\u00e4\u00e6"+
		"\u0003\b\u0004\u0000\u00e5\u00e4\u0001\u0000\u0000\u0000\u00e6\u00e7\u0001"+
		"\u0000\u0000\u0000\u00e7\u00e5\u0001\u0000\u0000\u0000\u00e7\u00e8\u0001"+
		"\u0000\u0000\u0000\u00e8\u00e9\u0001\u0000\u0000\u0000\u00e9\u00ea\u0005"+
		"\u0002\u0000\u0000\u00ea\u00ec\u0001\u0000\u0000\u0000\u00eb\u00e1\u0001"+
		"\u0000\u0000\u0000\u00eb\u00e2\u0001\u0000\u0000\u0000\u00ec\r\u0001\u0000"+
		"\u0000\u0000\u00ed\u00ee\u0005D\u0000\u0000\u00ee\u00f4\u0003X,\u0000"+
		"\u00ef\u00f1\u0005Z\u0000\u0000\u00f0\u00f2\u0003h4\u0000\u00f1\u00f0"+
		"\u0001\u0000\u0000\u0000\u00f1\u00f2\u0001\u0000\u0000\u0000\u00f2\u00f3"+
		"\u0001\u0000\u0000\u0000\u00f3\u00f5\u0005[\u0000\u0000\u00f4\u00ef\u0001"+
		"\u0000\u0000\u0000\u00f4\u00f5\u0001\u0000\u0000\u0000\u00f5\u00f6\u0001"+
		"\u0000\u0000\u0000\u00f6\u00f7\u0005\u0003\u0000\u0000\u00f7\u000f\u0001"+
		"\u0000\u0000\u0000\u00f8\u00f9\u0005\u000e\u0000\u0000\u00f9\u00fa\u0003"+
		">\u001f\u0000\u00fa\u00fb\u0005.\u0000\u0000\u00fb\u00fc\u0003\f\u0006"+
		"\u0000\u00fc\u0011\u0001\u0000\u0000\u0000\u00fd\u00fe\u0005\u000f\u0000"+
		"\u0000\u00fe\u00ff\u0005.\u0000\u0000\u00ff\u0100\u0003\f\u0006\u0000"+
		"\u0100\u0013\u0001\u0000\u0000\u0000\u0101\u0102\u0005\u0015\u0000\u0000"+
		"\u0102\u0103\u0005.\u0000\u0000\u0103\u0104\u0003\f\u0006\u0000\u0104"+
		"\u0015\u0001\u0000\u0000\u0000\u0105\u0108\u0003>\u001f\u0000\u0106\u0107"+
		"\u0005\n\u0000\u0000\u0107\u0109\u0003N\'\u0000\u0108\u0106\u0001\u0000"+
		"\u0000\u0000\u0108\u0109\u0001\u0000\u0000\u0000\u0109\u0017\u0001\u0000"+
		"\u0000\u0000\u010a\u0118\u0005\u0017\u0000\u0000\u010b\u0116\u0003>\u001f"+
		"\u0000\u010c\u010d\u0004\f\u0000\u0000\u010d\u010e\u0005-\u0000\u0000"+
		"\u010e\u010f\u0003Z-\u0000\u010f\u0110\u0006\f\uffff\uffff\u0000\u0110"+
		"\u0117\u0001\u0000\u0000\u0000\u0111\u0112\u0004\f\u0001\u0000\u0112\u0113"+
		"\u0005\n\u0000\u0000\u0113\u0114\u0003Z-\u0000\u0114\u0115\u0006\f\uffff"+
		"\uffff\u0000\u0115\u0117\u0001\u0000\u0000\u0000\u0116\u010c\u0001\u0000"+
		"\u0000\u0000\u0116\u0111\u0001\u0000\u0000\u0000\u0116\u0117\u0001\u0000"+
		"\u0000\u0000\u0117\u0119\u0001\u0000\u0000\u0000\u0118\u010b\u0001\u0000"+
		"\u0000\u0000\u0118\u0119\u0001\u0000\u0000\u0000\u0119\u011a\u0001\u0000"+
		"\u0000\u0000\u011a\u011b\u0005.\u0000\u0000\u011b\u011c\u0003\f\u0006"+
		"\u0000\u011c\u0019\u0001\u0000\u0000\u0000\u011d\u011e\u0005\u001d\u0000"+
		"\u0000\u011e\u0124\u0003Z-\u0000\u011f\u0121\u0005Z\u0000\u0000\u0120"+
		"\u0122\u0003h4\u0000\u0121\u0120\u0001\u0000\u0000\u0000\u0121\u0122\u0001"+
		"\u0000\u0000\u0000\u0122\u0123\u0001\u0000\u0000\u0000\u0123\u0125\u0005"+
		"[\u0000\u0000\u0124\u011f\u0001\u0000\u0000\u0000\u0124\u0125\u0001\u0000"+
		"\u0000\u0000\u0125\u0126\u0001\u0000\u0000\u0000\u0126\u0127\u0005.\u0000"+
		"\u0000\u0127\u0128\u0003\f\u0006\u0000\u0128\u001b\u0001\u0000\u0000\u0000"+
		"\u0129\u012b\u0005#\u0000\u0000\u012a\u0129\u0001\u0000\u0000\u0000\u012a"+
		"\u012b\u0001\u0000\u0000\u0000\u012b\u012c\u0001\u0000\u0000\u0000\u012c"+
		"\u012d\u0005\u0004\u0000\u0000\u012d\u012e\u0003Z-\u0000\u012e\u0130\u0005"+
		"Z\u0000\u0000\u012f\u0131\u0003\u001e\u000f\u0000\u0130\u012f\u0001\u0000"+
		"\u0000\u0000\u0130\u0131\u0001\u0000\u0000\u0000\u0131\u0132\u0001\u0000"+
		"\u0000\u0000\u0132\u0135\u0005[\u0000\u0000\u0133\u0134\u0005E\u0000\u0000"+
		"\u0134\u0136\u0003>\u001f\u0000\u0135\u0133\u0001\u0000\u0000\u0000\u0135"+
		"\u0136\u0001\u0000\u0000\u0000\u0136\u0137\u0001\u0000\u0000\u0000\u0137"+
		"\u0138\u0005.\u0000\u0000\u0138\u0139\u0003\f\u0006\u0000\u0139\u001d"+
		"\u0001\u0000\u0000\u0000\u013a\u013b\u0003$\u0012\u0000\u013b\u013c\u0005"+
		"-\u0000\u0000\u013c\u013e\u0001\u0000\u0000\u0000\u013d\u013a\u0001\u0000"+
		"\u0000\u0000\u013d\u013e\u0001\u0000\u0000\u0000\u013e\u0149\u0001\u0000"+
		"\u0000\u0000\u013f\u0142\u0003 \u0010\u0000\u0140\u0141\u0005-\u0000\u0000"+
		"\u0141\u0143\u0003$\u0012\u0000\u0142\u0140\u0001\u0000\u0000\u0000\u0142"+
		"\u0143\u0001\u0000\u0000\u0000\u0143\u0146\u0001\u0000\u0000\u0000\u0144"+
		"\u0145\u0005-\u0000\u0000\u0145\u0147\u0003\"\u0011\u0000\u0146\u0144"+
		"\u0001\u0000\u0000\u0000\u0146\u0147\u0001\u0000\u0000\u0000\u0147\u014a"+
		"\u0001\u0000\u0000\u0000\u0148\u014a\u0003\"\u0011\u0000\u0149\u013f\u0001"+
		"\u0000\u0000\u0000\u0149\u0148\u0001\u0000\u0000\u0000\u014a\u014c\u0001"+
		"\u0000\u0000\u0000\u014b\u014d\u0005-\u0000\u0000\u014c\u014b\u0001\u0000"+
		"\u0000\u0000\u014c\u014d\u0001\u0000\u0000\u0000\u014d\u0153\u0001\u0000"+
		"\u0000\u0000\u014e\u0150\u0003$\u0012\u0000\u014f\u0151\u0005-\u0000\u0000"+
		"\u0150\u014f\u0001\u0000\u0000\u0000\u0150\u0151\u0001\u0000\u0000\u0000"+
		"\u0151\u0153\u0001\u0000\u0000\u0000\u0152\u013d\u0001\u0000\u0000\u0000"+
		"\u0152\u014e\u0001\u0000\u0000\u0000\u0153\u001f\u0001\u0000\u0000\u0000"+
		"\u0154\u0155\u0005,\u0000\u0000\u0155\u0156\u0003(\u0014\u0000\u0156!"+
		"\u0001\u0000\u0000\u0000\u0157\u0158\u00050\u0000\u0000\u0158\u0159\u0003"+
		"(\u0014\u0000\u0159#\u0001\u0000\u0000\u0000\u015a\u015f\u0003&\u0013"+
		"\u0000\u015b\u015c\u0005-\u0000\u0000\u015c\u015e\u0003&\u0013\u0000\u015d"+
		"\u015b\u0001\u0000\u0000\u0000\u015e\u0161\u0001\u0000\u0000\u0000\u015f"+
		"\u015d\u0001\u0000\u0000\u0000\u015f\u0160\u0001\u0000\u0000\u0000\u0160"+
		"%\u0001\u0000\u0000\u0000\u0161\u015f\u0001\u0000\u0000\u0000\u0162\u0165"+
		"\u0003(\u0014\u0000\u0163\u0164\u00051\u0000\u0000\u0164\u0166\u0003>"+
		"\u001f\u0000\u0165\u0163\u0001\u0000\u0000\u0000\u0165\u0166\u0001\u0000"+
		"\u0000\u0000\u0166\u0169\u0001\u0000\u0000\u0000\u0167\u0169\u0005,\u0000"+
		"\u0000\u0168\u0162\u0001\u0000\u0000\u0000\u0168\u0167\u0001\u0000\u0000"+
		"\u0000\u0169\'\u0001\u0000\u0000\u0000\u016a\u016d\u0003Z-\u0000\u016b"+
		"\u016c\u0005.\u0000\u0000\u016c\u016e\u0003>\u001f\u0000\u016d\u016b\u0001"+
		"\u0000\u0000\u0000\u016d\u016e\u0001\u0000\u0000\u0000\u016e)\u0001\u0000"+
		"\u0000\u0000\u016f\u0174\u0003,\u0016\u0000\u0170\u0171\u0005/\u0000\u0000"+
		"\u0171\u0173\u0003,\u0016\u0000\u0172\u0170\u0001\u0000\u0000\u0000\u0173"+
		"\u0176\u0001\u0000\u0000\u0000\u0174\u0172\u0001\u0000\u0000\u0000\u0174"+
		"\u0175\u0001\u0000\u0000\u0000\u0175\u0178\u0001\u0000\u0000\u0000\u0176"+
		"\u0174\u0001\u0000\u0000\u0000\u0177\u0179\u0005/\u0000\u0000\u0178\u0177"+
		"\u0001\u0000\u0000\u0000\u0178\u0179\u0001\u0000\u0000\u0000\u0179\u017a"+
		"\u0001\u0000\u0000\u0000\u017a\u017b\u0007\u0000\u0000\u0000\u017b+\u0001"+
		"\u0000\u0000\u0000\u017c\u017e\u0003.\u0017\u0000\u017d\u017f\u00032\u0019"+
		"\u0000\u017e\u017d\u0001\u0000\u0000\u0000\u017e\u017f\u0001\u0000\u0000"+
		"\u0000\u017f\u01f9\u0001\u0000\u0000\u0000\u0180\u0181\u0004\u0016\u0002"+
		"\u0000\u0181\u0198\u0005%\u0000\u0000\u0182\u0187\u0003>\u001f\u0000\u0183"+
		"\u0184\u0005-\u0000\u0000\u0184\u0186\u0003>\u001f\u0000\u0185\u0183\u0001"+
		"\u0000\u0000\u0000\u0186\u0189\u0001\u0000\u0000\u0000\u0187\u0185\u0001"+
		"\u0000\u0000\u0000\u0187\u0188\u0001\u0000\u0000\u0000\u0188\u018b\u0001"+
		"\u0000\u0000\u0000\u0189\u0187\u0001\u0000\u0000\u0000\u018a\u018c\u0005"+
		"-\u0000\u0000\u018b\u018a\u0001\u0000\u0000\u0000\u018b\u018c\u0001\u0000"+
		"\u0000\u0000\u018c\u0199\u0001\u0000\u0000\u0000\u018d\u018e\u00056\u0000"+
		"\u0000\u018e\u0191\u0003>\u001f\u0000\u018f\u0190\u0005-\u0000\u0000\u0190"+
		"\u0192\u0003>\u001f\u0000\u0191\u018f\u0001\u0000\u0000\u0000\u0192\u0193"+
		"\u0001\u0000\u0000\u0000\u0193\u0191\u0001\u0000\u0000\u0000\u0193\u0194"+
		"\u0001\u0000\u0000\u0000\u0194\u0196\u0001\u0000\u0000\u0000\u0195\u0197"+
		"\u0005-\u0000\u0000\u0196\u0195\u0001\u0000\u0000\u0000\u0196\u0197\u0001"+
		"\u0000\u0000\u0000\u0197\u0199\u0001\u0000\u0000\u0000\u0198\u0182\u0001"+
		"\u0000\u0000\u0000\u0198\u018d\u0001\u0000\u0000\u0000\u0199\u019a\u0001"+
		"\u0000\u0000\u0000\u019a\u019b\u0006\u0016\uffff\uffff\u0000\u019b\u01f9"+
		"\u0001\u0000\u0000\u0000\u019c\u019d\u0005\u001f\u0000\u0000\u019d\u01f9"+
		"\u00034\u001a\u0000\u019e\u01f9\u0005 \u0000\u0000\u019f\u01f9\u0005\""+
		"\u0000\u0000\u01a0\u01f9\u0005!\u0000\u0000\u01a1\u01a3\u0005\u0005\u0000"+
		"\u0000\u01a2\u01a4\u0003V+\u0000\u01a3\u01a2\u0001\u0000\u0000\u0000\u01a3"+
		"\u01a4\u0001\u0000\u0000\u0000\u01a4\u01f9\u0001\u0000\u0000\u0000\u01a5"+
		"\u01af\u0005\u0006\u0000\u0000\u01a6\u01ad\u0003>\u001f\u0000\u01a7\u01a8"+
		"\u0005-\u0000\u0000\u01a8\u01ab\u0003>\u001f\u0000\u01a9\u01aa\u0005-"+
		"\u0000\u0000\u01aa\u01ac\u0003>\u001f\u0000\u01ab\u01a9\u0001\u0000\u0000"+
		"\u0000\u01ab\u01ac\u0001\u0000\u0000\u0000\u01ac\u01ae\u0001\u0000\u0000"+
		"\u0000\u01ad\u01a7\u0001\u0000\u0000\u0000\u01ad\u01ae\u0001\u0000\u0000"+
		"\u0000\u01ae\u01b0\u0001\u0000\u0000\u0000\u01af\u01a6\u0001\u0000\u0000"+
		"\u0000\u01af\u01b0\u0001\u0000\u0000\u0000\u01b0\u01b3\u0001\u0000\u0000"+
		"\u0000\u01b1\u01b2\u0005\u0007\u0000\u0000\u01b2\u01b4\u0003>\u001f\u0000"+
		"\u01b3\u01b1\u0001\u0000\u0000\u0000\u01b3\u01b4\u0001\u0000\u0000\u0000"+
		"\u01b4\u01f9\u0001\u0000\u0000\u0000\u01b5\u01f9\u0003`0\u0000\u01b6\u01b7"+
		"\u0005\b\u0000\u0000\u01b7\u01f9\u0003:\u001d\u0000\u01b8\u01c5\u0005"+
		"\u0007\u0000\u0000\u01b9\u01bb\u0007\u0001\u0000\u0000\u01ba\u01b9\u0001"+
		"\u0000\u0000\u0000\u01bb\u01be\u0001\u0000\u0000\u0000\u01bc\u01ba\u0001"+
		"\u0000\u0000\u0000\u01bc\u01bd\u0001\u0000\u0000\u0000\u01bd\u01bf\u0001"+
		"\u0000\u0000\u0000\u01be\u01bc\u0001\u0000\u0000\u0000\u01bf\u01c6\u0003"+
		"X,\u0000\u01c0\u01c2\u0007\u0001\u0000\u0000\u01c1\u01c0\u0001\u0000\u0000"+
		"\u0000\u01c2\u01c3\u0001\u0000\u0000\u0000\u01c3\u01c1\u0001\u0000\u0000"+
		"\u0000\u01c3\u01c4\u0001\u0000\u0000\u0000\u01c4\u01c6\u0001\u0000\u0000"+
		"\u0000\u01c5\u01bc\u0001\u0000\u0000\u0000\u01c5\u01c1\u0001\u0000\u0000"+
		"\u0000\u01c6\u01c7\u0001\u0000\u0000\u0000\u01c7\u01ce\u0005\b\u0000\u0000"+
		"\u01c8\u01cf\u0005,\u0000\u0000\u01c9\u01ca\u0005Z\u0000\u0000\u01ca\u01cb"+
		"\u00036\u001b\u0000\u01cb\u01cc\u0005[\u0000\u0000\u01cc\u01cf\u0001\u0000"+
		"\u0000\u0000\u01cd\u01cf\u00036\u001b\u0000\u01ce\u01c8\u0001\u0000\u0000"+
		"\u0000\u01ce\u01c9\u0001\u0000\u0000\u0000\u01ce\u01cd\u0001\u0000\u0000"+
		"\u0000\u01cf\u01f9\u0001\u0000\u0000\u0000\u01d0\u01d1\u0005\u000b\u0000"+
		"\u0000\u01d1\u01d6\u0003Z-\u0000\u01d2\u01d3\u0005-\u0000\u0000\u01d3"+
		"\u01d5\u0003Z-\u0000\u01d4\u01d2\u0001\u0000\u0000\u0000\u01d5\u01d8\u0001"+
		"\u0000\u0000\u0000\u01d6\u01d4\u0001\u0000\u0000\u0000\u01d6\u01d7\u0001"+
		"\u0000\u0000\u0000\u01d7\u01f9\u0001\u0000\u0000\u0000\u01d8\u01d6\u0001"+
		"\u0000\u0000\u0000\u01d9\u01da\u0004\u0016\u0003\u0000\u01da\u01db\u0005"+
		"&\u0000\u0000\u01db\u01e2\u0003N\'\u0000\u01dc\u01dd\u0005\u0012\u0000"+
		"\u0000\u01dd\u01e0\u0003>\u001f\u0000\u01de\u01df\u0005-\u0000\u0000\u01df"+
		"\u01e1\u0003>\u001f\u0000\u01e0\u01de\u0001\u0000\u0000\u0000\u01e0\u01e1"+
		"\u0001\u0000\u0000\u0000\u01e1\u01e3\u0001\u0000\u0000\u0000\u01e2\u01dc"+
		"\u0001\u0000\u0000\u0000\u01e2\u01e3\u0001\u0000\u0000\u0000\u01e3\u01e4"+
		"\u0001\u0000\u0000\u0000\u01e4\u01e5\u0006\u0016\uffff\uffff\u0000\u01e5"+
		"\u01f9\u0001\u0000\u0000\u0000\u01e6\u01e7\u0005\f\u0000\u0000\u01e7\u01ea"+
		"\u0003>\u001f\u0000\u01e8\u01e9\u0005-\u0000\u0000\u01e9\u01eb\u0003>"+
		"\u001f\u0000\u01ea\u01e8\u0001\u0000\u0000\u0000\u01ea\u01eb\u0001\u0000"+
		"\u0000\u0000\u01eb\u01f9\u0001\u0000\u0000\u0000\u01ec\u01ed\u0004\u0016"+
		"\u0004\u0000\u01ed\u01ee\u0005\t\u0000\u0000\u01ee\u01f3\u0003Z-\u0000"+
		"\u01ef\u01f0\u0005-\u0000\u0000\u01f0\u01f2\u0003Z-\u0000\u01f1\u01ef"+
		"\u0001\u0000\u0000\u0000\u01f2\u01f5\u0001\u0000\u0000\u0000\u01f3\u01f1"+
		"\u0001\u0000\u0000\u0000\u01f3\u01f4\u0001\u0000\u0000\u0000\u01f4\u01f6"+
		"\u0001\u0000\u0000\u0000\u01f5\u01f3\u0001\u0000\u0000\u0000\u01f6\u01f7"+
		"\u0006\u0016\uffff\uffff\u0000\u01f7\u01f9\u0001\u0000\u0000\u0000\u01f8"+
		"\u017c\u0001\u0000\u0000\u0000\u01f8\u0180\u0001\u0000\u0000\u0000\u01f8"+
		"\u019c\u0001\u0000\u0000\u0000\u01f8\u019e\u0001\u0000\u0000\u0000\u01f8"+
		"\u019f\u0001\u0000\u0000\u0000\u01f8\u01a0\u0001\u0000\u0000\u0000\u01f8"+
		"\u01a1\u0001\u0000\u0000\u0000\u01f8\u01a5\u0001\u0000\u0000\u0000\u01f8"+
		"\u01b5\u0001\u0000\u0000\u0000\u01f8\u01b6\u0001\u0000\u0000\u0000\u01f8"+
		"\u01b8\u0001\u0000\u0000\u0000\u01f8\u01d0\u0001\u0000\u0000\u0000\u01f8"+
		"\u01d9\u0001\u0000\u0000\u0000\u01f8\u01e6\u0001\u0000\u0000\u0000\u01f8"+
		"\u01ec\u0001\u0000\u0000\u0000\u01f9-\u0001\u0000\u0000\u0000\u01fa\u01fd"+
		"\u0003>\u001f\u0000\u01fb\u01fd\u00030\u0018\u0000\u01fc\u01fa\u0001\u0000"+
		"\u0000\u0000\u01fc\u01fb\u0001\u0000\u0000\u0000\u01fd\u01fe\u0001\u0000"+
		"\u0000\u0000\u01fe\u01ff\u0005-\u0000\u0000\u01ff\u0201\u0001\u0000\u0000"+
		"\u0000\u0200\u01fc\u0001\u0000\u0000\u0000\u0201\u0202\u0001\u0000\u0000"+
		"\u0000\u0202\u0200\u0001\u0000\u0000\u0000\u0202\u0203\u0001\u0000\u0000"+
		"\u0000\u0203\u0206\u0001\u0000\u0000\u0000\u0204\u0207\u0003>\u001f\u0000"+
		"\u0205\u0207\u00030\u0018\u0000\u0206\u0204\u0001\u0000\u0000\u0000\u0206"+
		"\u0205\u0001\u0000\u0000\u0000\u0206\u0207\u0001\u0000\u0000\u0000\u0207"+
		"\u020a\u0001\u0000\u0000\u0000\u0208\u020a\u0003V+\u0000\u0209\u0200\u0001"+
		"\u0000\u0000\u0000\u0209\u0208\u0001\u0000\u0000\u0000\u020a/\u0001\u0000"+
		"\u0000\u0000\u020b\u020c\u0005,\u0000\u0000\u020c\u020d\u0003N\'\u0000"+
		"\u020d1\u0001\u0000\u0000\u0000\u020e\u021c\u00051\u0000\u0000\u020f\u0214"+
		"\u0003.\u0017\u0000\u0210\u0211\u00051\u0000\u0000\u0211\u0213\u0003."+
		"\u0017\u0000\u0212\u0210\u0001\u0000\u0000\u0000\u0213\u0216\u0001\u0000"+
		"\u0000\u0000\u0214\u0212\u0001\u0000\u0000\u0000\u0214\u0215\u0001\u0000"+
		"\u0000\u0000\u0215\u0219\u0001\u0000\u0000\u0000\u0216\u0214\u0001\u0000"+
		"\u0000\u0000\u0217\u0218\u00051\u0000\u0000\u0218\u021a\u0003`0\u0000"+
		"\u0219\u0217\u0001\u0000\u0000\u0000\u0219\u021a\u0001\u0000\u0000\u0000"+
		"\u021a\u021d\u0001\u0000\u0000\u0000\u021b\u021d\u0003`0\u0000\u021c\u020f"+
		"\u0001\u0000\u0000\u0000\u021c\u021b\u0001\u0000\u0000\u0000\u021d\u022d"+
		"\u0001\u0000\u0000\u0000\u021e\u021f\u0004\u0019\u0005\u0000\u021f\u0220"+
		"\u0005.\u0000\u0000\u0220\u0223\u0003>\u001f\u0000\u0221\u0222\u00051"+
		"\u0000\u0000\u0222\u0224\u0003V+\u0000\u0223\u0221\u0001\u0000\u0000\u0000"+
		"\u0223\u0224\u0001\u0000\u0000\u0000\u0224\u0225\u0001\u0000\u0000\u0000"+
		"\u0225\u0226\u0006\u0019\uffff\uffff\u0000\u0226\u022d\u0001\u0000\u0000"+
		"\u0000\u0227\u022a\u0007\u0002\u0000\u0000\u0228\u022b\u0003`0\u0000\u0229"+
		"\u022b\u0003V+\u0000\u022a\u0228\u0001\u0000\u0000\u0000\u022a\u0229\u0001"+
		"\u0000\u0000\u0000\u022b\u022d\u0001\u0000\u0000\u0000\u022c\u020e\u0001"+
		"\u0000\u0000\u0000\u022c\u021e\u0001\u0000\u0000\u0000\u022c\u0227\u0001"+
		"\u0000\u0000\u0000\u022d3\u0001\u0000\u0000\u0000\u022e\u0233\u0003N\'"+
		"\u0000\u022f\u0230\u0005-\u0000\u0000\u0230\u0232\u0003N\'\u0000\u0231"+
		"\u022f\u0001\u0000\u0000\u0000\u0232\u0235\u0001\u0000\u0000\u0000\u0233"+
		"\u0231\u0001\u0000\u0000\u0000\u0233\u0234\u0001\u0000\u0000\u0000\u0234"+
		"\u0237\u0001\u0000\u0000\u0000\u0235\u0233\u0001\u0000\u0000\u0000\u0236"+
		"\u0238\u0005-\u0000\u0000\u0237\u0236\u0001\u0000\u0000\u0000\u0237\u0238"+
		"\u0001\u0000\u0000\u0000\u02385\u0001\u0000\u0000\u0000\u0239\u023e\u0003"+
		"8\u001c\u0000\u023a\u023b\u0005-\u0000\u0000\u023b\u023d\u00038\u001c"+
		"\u0000\u023c\u023a\u0001\u0000\u0000\u0000\u023d\u0240\u0001\u0000\u0000"+
		"\u0000\u023e\u023c\u0001\u0000\u0000\u0000\u023e\u023f\u0001\u0000\u0000"+
		"\u0000\u023f\u0242\u0001\u0000\u0000\u0000\u0240\u023e\u0001\u0000\u0000"+
		"\u0000\u0241\u0243\u0005-\u0000\u0000\u0242\u0241\u0001\u0000\u0000\u0000"+
		"\u0242\u0243\u0001\u0000\u0000\u0000\u02437\u0001\u0000\u0000\u0000\u0244"+
		"\u0247\u0003Z-\u0000\u0245\u0246\u0005\n\u0000\u0000\u0246\u0248\u0003"+
		"Z-\u0000\u0247\u0245\u0001\u0000\u0000\u0000\u0247\u0248\u0001\u0000\u0000"+
		"\u0000\u02489\u0001\u0000\u0000\u0000\u0249\u024e\u0003<\u001e\u0000\u024a"+
		"\u024b\u0005-\u0000\u0000\u024b\u024d\u0003<\u001e\u0000\u024c\u024a\u0001"+
		"\u0000\u0000\u0000\u024d\u0250\u0001\u0000\u0000\u0000\u024e\u024c\u0001"+
		"\u0000\u0000\u0000\u024e\u024f\u0001\u0000\u0000\u0000\u024f;\u0001\u0000"+
		"\u0000\u0000\u0250\u024e\u0001\u0000\u0000\u0000\u0251\u0254\u0003X,\u0000"+
		"\u0252\u0253\u0005\n\u0000\u0000\u0253\u0255\u0003Z-\u0000\u0254\u0252"+
		"\u0001\u0000\u0000\u0000\u0254\u0255\u0001\u0000\u0000\u0000\u0255=\u0001"+
		"\u0000\u0000\u0000\u0256\u025c\u0003J%\u0000\u0257\u0258\u0005\r\u0000"+
		"\u0000\u0258\u0259\u0003J%\u0000\u0259\u025a\u0005\u000f\u0000\u0000\u025a"+
		"\u025b\u0003>\u001f\u0000\u025b\u025d\u0001\u0000\u0000\u0000\u025c\u0257"+
		"\u0001\u0000\u0000\u0000\u025c\u025d\u0001\u0000\u0000\u0000\u025d\u0265"+
		"\u0001\u0000\u0000\u0000\u025e\u0260\u0005\u0018\u0000\u0000\u025f\u0261"+
		"\u0003@ \u0000\u0260\u025f\u0001\u0000\u0000\u0000\u0260\u0261\u0001\u0000"+
		"\u0000\u0000\u0261\u0262\u0001\u0000\u0000\u0000\u0262\u0263\u0005.\u0000"+
		"\u0000\u0263\u0265\u0003>\u001f\u0000\u0264\u0256\u0001\u0000\u0000\u0000"+
		"\u0264\u025e\u0001\u0000\u0000\u0000\u0265?\u0001\u0000\u0000\u0000\u0266"+
		"\u0267\u0003B!\u0000\u0267\u0268\u0005-\u0000\u0000\u0268\u026a\u0001"+
		"\u0000\u0000\u0000\u0269\u0266\u0001\u0000\u0000\u0000\u0269\u026a\u0001"+
		"\u0000\u0000\u0000\u026a\u0275\u0001\u0000\u0000\u0000\u026b\u026e\u0003"+
		"F#\u0000\u026c\u026d\u0005-\u0000\u0000\u026d\u026f\u0003B!\u0000\u026e"+
		"\u026c\u0001\u0000\u0000\u0000\u026e\u026f\u0001\u0000\u0000\u0000\u026f"+
		"\u0272\u0001\u0000\u0000\u0000\u0270\u0271\u0005-\u0000\u0000\u0271\u0273"+
		"\u0003H$\u0000\u0272\u0270\u0001\u0000\u0000\u0000\u0272\u0273\u0001\u0000"+
		"\u0000\u0000\u0273\u0276\u0001\u0000\u0000\u0000\u0274\u0276\u0003H$\u0000"+
		"\u0275\u026b\u0001\u0000\u0000\u0000\u0275\u0274\u0001\u0000\u0000\u0000"+
		"\u0276\u0278\u0001\u0000\u0000\u0000\u0277\u0279\u0005-\u0000\u0000\u0278"+
		"\u0277\u0001\u0000\u0000\u0000\u0278\u0279\u0001\u0000\u0000\u0000\u0279"+
		"\u027f\u0001\u0000\u0000\u0000\u027a\u027c\u0003B!\u0000\u027b\u027d\u0005"+
		"-\u0000\u0000\u027c\u027b\u0001\u0000\u0000\u0000\u027c\u027d\u0001\u0000"+
		"\u0000\u0000\u027d\u027f\u0001\u0000\u0000\u0000\u027e\u0269\u0001\u0000"+
		"\u0000\u0000\u027e\u027a\u0001\u0000\u0000\u0000\u027fA\u0001\u0000\u0000"+
		"\u0000\u0280\u0285\u0003D\"\u0000\u0281\u0282\u0005-\u0000\u0000\u0282"+
		"\u0284\u0003D\"\u0000\u0283\u0281\u0001\u0000\u0000\u0000\u0284\u0287"+
		"\u0001\u0000\u0000\u0000\u0285\u0283\u0001\u0000\u0000\u0000\u0285\u0286"+
		"\u0001\u0000\u0000\u0000\u0286C\u0001\u0000\u0000\u0000\u0287\u0285\u0001"+
		"\u0000\u0000\u0000\u0288\u028b\u0003Z-\u0000\u0289\u028a\u00051\u0000"+
		"\u0000\u028a\u028c\u0003>\u001f\u0000\u028b\u0289\u0001\u0000\u0000\u0000"+
		"\u028b\u028c\u0001\u0000\u0000\u0000\u028c\u028f\u0001\u0000\u0000\u0000"+
		"\u028d\u028f\u0005,\u0000\u0000\u028e\u0288\u0001\u0000\u0000\u0000\u028e"+
		"\u028d\u0001\u0000\u0000\u0000\u028fE\u0001\u0000\u0000\u0000\u0290\u0291"+
		"\u0005,\u0000\u0000\u0291\u0292\u0003Z-\u0000\u0292G\u0001\u0000\u0000"+
		"\u0000\u0293\u0294\u00050\u0000\u0000\u0294\u0295\u0003Z-\u0000\u0295"+
		"I\u0001\u0000\u0000\u0000\u0296\u0297\u0006%\uffff\uffff\u0000\u0297\u029b"+
		"\u0003L&\u0000\u0298\u0299\u0005\u001b\u0000\u0000\u0299\u029b\u0003J"+
		"%\u0003\u029a\u0296\u0001\u0000\u0000\u0000\u029a\u0298\u0001\u0000\u0000"+
		"\u0000\u029b\u02a4\u0001\u0000\u0000\u0000\u029c\u029d\n\u0002\u0000\u0000"+
		"\u029d\u029e\u0005\u001a\u0000\u0000\u029e\u02a3\u0003J%\u0003\u029f\u02a0"+
		"\n\u0001\u0000\u0000\u02a0\u02a1\u0005\u0019\u0000\u0000\u02a1\u02a3\u0003"+
		"J%\u0002\u02a2\u029c\u0001\u0000\u0000\u0000\u02a2\u029f\u0001\u0000\u0000"+
		"\u0000\u02a3\u02a6\u0001\u0000\u0000\u0000\u02a4\u02a2\u0001\u0000\u0000"+
		"\u0000\u02a4\u02a5\u0001\u0000\u0000\u0000\u02a5K\u0001\u0000\u0000\u0000"+
		"\u02a6\u02a4\u0001\u0000\u0000\u0000\u02a7\u02a8\u0006&\uffff\uffff\u0000"+
		"\u02a8\u02a9\u0003N\'\u0000\u02a9\u02bf\u0001\u0000\u0000\u0000\u02aa"+
		"\u02ba\n\u0002\u0000\u0000\u02ab\u02bb\u0005=\u0000\u0000\u02ac\u02bb"+
		"\u0005>\u0000\u0000\u02ad\u02bb\u0005?\u0000\u0000\u02ae\u02bb\u0005@"+
		"\u0000\u0000\u02af\u02bb\u0005A\u0000\u0000\u02b0\u02bb\u0005B\u0000\u0000"+
		"\u02b1\u02bb\u0005C\u0000\u0000\u02b2\u02b4\u0005\u001b\u0000\u0000\u02b3"+
		"\u02b2\u0001\u0000\u0000\u0000\u02b3\u02b4\u0001\u0000\u0000\u0000\u02b4"+
		"\u02b5\u0001\u0000\u0000\u0000\u02b5\u02bb\u0005\u0012\u0000\u0000\u02b6"+
		"\u02b8\u0005\u001c\u0000\u0000\u02b7\u02b9\u0005\u001b\u0000\u0000\u02b8"+
		"\u02b7\u0001\u0000\u0000\u0000\u02b8\u02b9\u0001\u0000\u0000\u0000\u02b9"+
		"\u02bb\u0001\u0000\u0000\u0000\u02ba\u02ab\u0001\u0000\u0000\u0000\u02ba"+
		"\u02ac\u0001\u0000\u0000\u0000\u02ba\u02ad\u0001\u0000\u0000\u0000\u02ba"+
		"\u02ae\u0001\u0000\u0000\u0000\u02ba\u02af\u0001\u0000\u0000\u0000\u02ba"+
		"\u02b0\u0001\u0000\u0000\u0000\u02ba\u02b1\u0001\u0000\u0000\u0000\u02ba"+
		"\u02b3\u0001\u0000\u0000\u0000\u02ba\u02b6\u0001\u0000\u0000\u0000\u02bb"+
		"\u02bc\u0001\u0000\u0000\u0000\u02bc\u02be\u0003L&\u0003\u02bd\u02aa\u0001"+
		"\u0000\u0000\u0000\u02be\u02c1\u0001\u0000\u0000\u0000\u02bf\u02bd\u0001"+
		"\u0000\u0000\u0000\u02bf\u02c0\u0001\u0000\u0000\u0000\u02c0M\u0001\u0000"+
		"\u0000\u0000\u02c1\u02bf\u0001\u0000\u0000\u0000\u02c2\u02c4\u0006\'\uffff"+
		"\uffff\u0000\u02c3\u02c5\u0005$\u0000\u0000\u02c4\u02c3\u0001\u0000\u0000"+
		"\u0000\u02c4\u02c5\u0001\u0000\u0000\u0000\u02c5\u02c6\u0001\u0000\u0000"+
		"\u0000\u02c6\u02ca\u0003P(\u0000\u02c7\u02c9\u0003d2\u0000\u02c8\u02c7"+
		"\u0001\u0000\u0000\u0000\u02c9\u02cc\u0001\u0000\u0000\u0000\u02ca\u02c8"+
		"\u0001\u0000\u0000\u0000\u02ca\u02cb\u0001\u0000\u0000\u0000\u02cb\u02d0"+
		"\u0001\u0000\u0000\u0000\u02cc\u02ca\u0001\u0000\u0000\u0000\u02cd\u02ce"+
		"\u0007\u0003\u0000\u0000\u02ce\u02d0\u0003N\'\u0007\u02cf\u02c2\u0001"+
		"\u0000\u0000\u0000\u02cf\u02cd\u0001\u0000\u0000\u0000\u02d0\u02e8\u0001"+
		"\u0000\u0000\u0000\u02d1\u02d2\n\b\u0000\u0000\u02d2\u02d3\u00050\u0000"+
		"\u0000\u02d3\u02e7\u0003N\'\b\u02d4\u02d5\n\u0006\u0000\u0000\u02d5\u02d6"+
		"\u0007\u0004\u0000\u0000\u02d6\u02e7\u0003N\'\u0007\u02d7\u02d8\n\u0005"+
		"\u0000\u0000\u02d8\u02d9\u0007\u0005\u0000\u0000\u02d9\u02e7\u0003N\'"+
		"\u0006\u02da\u02db\n\u0004\u0000\u0000\u02db\u02dc\u0007\u0006\u0000\u0000"+
		"\u02dc\u02e7\u0003N\'\u0005\u02dd\u02de\n\u0003\u0000\u0000\u02de\u02df"+
		"\u00054\u0000\u0000\u02df\u02e7\u0003N\'\u0004\u02e0\u02e1\n\u0002\u0000"+
		"\u0000\u02e1\u02e2\u00053\u0000\u0000\u02e2\u02e7\u0003N\'\u0003\u02e3"+
		"\u02e4\n\u0001\u0000\u0000\u02e4\u02e5\u00052\u0000\u0000\u02e5\u02e7"+
		"\u0003N\'\u0002\u02e6\u02d1\u0001\u0000\u0000\u0000\u02e6\u02d4\u0001"+
		"\u0000\u0000\u0000\u02e6\u02d7\u0001\u0000\u0000\u0000\u02e6\u02da\u0001"+
		"\u0000\u0000\u0000\u02e6\u02dd\u0001\u0000\u0000\u0000\u02e6\u02e0\u0001"+
		"\u0000\u0000\u0000\u02e6\u02e3\u0001\u0000\u0000\u0000\u02e7\u02ea\u0001"+
		"\u0000\u0000\u0000\u02e8\u02e6\u0001\u0000\u0000\u0000\u02e8\u02e9\u0001"+
		"\u0000\u0000\u0000\u02e9O\u0001\u0000\u0000\u0000\u02ea\u02e8\u0001\u0000"+
		"\u0000\u0000\u02eb\u02ee\u0005Z\u0000\u0000\u02ec\u02ef\u0003`0\u0000"+
		"\u02ed\u02ef\u0003T*\u0000\u02ee\u02ec\u0001\u0000\u0000\u0000\u02ee\u02ed"+
		"\u0001\u0000\u0000\u0000\u02ee\u02ef\u0001\u0000\u0000\u0000\u02ef\u02f0"+
		"\u0001\u0000\u0000\u0000\u02f0\u0311\u0005[\u0000\u0000\u02f1\u02f3\u0005"+
		"^\u0000\u0000\u02f2\u02f4\u0003T*\u0000\u02f3\u02f2\u0001\u0000\u0000"+
		"\u0000\u02f3\u02f4\u0001\u0000\u0000\u0000\u02f4\u02f5\u0001\u0000\u0000"+
		"\u0000\u02f5\u0311\u0005_\u0000\u0000\u02f6\u02f8\u0005\\\u0000\u0000"+
		"\u02f7\u02f9\u0003R)\u0000\u02f8\u02f7\u0001\u0000\u0000\u0000\u02f8\u02f9"+
		"\u0001\u0000\u0000\u0000\u02f9\u02fa\u0001\u0000\u0000\u0000\u02fa\u0311"+
		"\u0005]\u0000\u0000\u02fb\u02fc\u0005+\u0000\u0000\u02fc\u02fe\u0003V"+
		"+\u0000\u02fd\u02ff\u0005-\u0000\u0000\u02fe\u02fd\u0001\u0000\u0000\u0000"+
		"\u02fe\u02ff\u0001\u0000\u0000\u0000\u02ff\u0300\u0001\u0000\u0000\u0000"+
		"\u0300\u0301\u0005+\u0000\u0000\u0301\u0311\u0001\u0000\u0000\u0000\u0302"+
		"\u0311\u0005*\u0000\u0000\u0303\u0311\u0003Z-\u0000\u0304\u0311\u0005"+
		"%\u0000\u0000\u0305\u0311\u0005&\u0000\u0000\u0306\u0308\u00058\u0000"+
		"\u0000\u0307\u0306\u0001\u0000\u0000\u0000\u0307\u0308\u0001\u0000\u0000"+
		"\u0000\u0308\u0309\u0001\u0000\u0000\u0000\u0309\u0311\u0003\\.\u0000"+
		"\u030a\u0311\u0005\u0014\u0000\u0000\u030b\u030d\u0005S\u0000\u0000\u030c"+
		"\u030b\u0001\u0000\u0000\u0000\u030d\u030e\u0001\u0000\u0000\u0000\u030e"+
		"\u030c\u0001\u0000\u0000\u0000\u030e\u030f\u0001\u0000\u0000\u0000\u030f"+
		"\u0311\u0001\u0000\u0000\u0000\u0310\u02eb\u0001\u0000\u0000\u0000\u0310"+
		"\u02f1\u0001\u0000\u0000\u0000\u0310\u02f6\u0001\u0000\u0000\u0000\u0310"+
		"\u02fb\u0001\u0000\u0000\u0000\u0310\u0302\u0001\u0000\u0000\u0000\u0310"+
		"\u0303\u0001\u0000\u0000\u0000\u0310\u0304\u0001\u0000\u0000\u0000\u0310"+
		"\u0305\u0001\u0000\u0000\u0000\u0310\u0307\u0001\u0000\u0000\u0000\u0310"+
		"\u030a\u0001\u0000\u0000\u0000\u0310\u030c\u0001\u0000\u0000\u0000\u0311"+
		"Q\u0001\u0000\u0000\u0000\u0312\u0313\u0003>\u001f\u0000\u0313\u0314\u0005"+
		".\u0000\u0000\u0314\u0315\u0003>\u001f\u0000\u0315\u0319\u0001\u0000\u0000"+
		"\u0000\u0316\u0317\u00050\u0000\u0000\u0317\u0319\u0003N\'\u0000\u0318"+
		"\u0312\u0001\u0000\u0000\u0000\u0318\u0316\u0001\u0000\u0000\u0000\u0319"+
		"\u0325\u0001\u0000\u0000\u0000\u031a\u0321\u0005-\u0000\u0000\u031b\u031c"+
		"\u0003>\u001f\u0000\u031c\u031d\u0005.\u0000\u0000\u031d\u031e\u0003>"+
		"\u001f\u0000\u031e\u0322\u0001\u0000\u0000\u0000\u031f\u0320\u00050\u0000"+
		"\u0000\u0320\u0322\u0003N\'\u0000\u0321\u031b\u0001\u0000\u0000\u0000"+
		"\u0321\u031f\u0001\u0000\u0000\u0000\u0322\u0324\u0001\u0000\u0000\u0000"+
		"\u0323\u031a\u0001\u0000\u0000\u0000\u0324\u0327\u0001\u0000\u0000\u0000"+
		"\u0325\u0323\u0001\u0000\u0000\u0000\u0325\u0326\u0001\u0000\u0000\u0000"+
		"\u0326\u0329\u0001\u0000\u0000\u0000\u0327\u0325\u0001\u0000\u0000\u0000"+
		"\u0328\u032a\u0005-\u0000\u0000\u0329\u0328\u0001\u0000\u0000\u0000\u0329"+
		"\u032a\u0001\u0000\u0000\u0000\u032a\u0332\u0001\u0000\u0000\u0000\u032b"+
		"\u032c\u0003>\u001f\u0000\u032c\u032d\u0005.\u0000\u0000\u032d\u032e\u0003"+
		">\u001f\u0000\u032e\u032f\u0003r9\u0000\u032f\u0332\u0001\u0000\u0000"+
		"\u0000\u0330\u0332\u0003T*\u0000\u0331\u0318\u0001\u0000\u0000\u0000\u0331"+
		"\u032b\u0001\u0000\u0000\u0000\u0331\u0330\u0001\u0000\u0000\u0000\u0332"+
		"S\u0001\u0000\u0000\u0000\u0333\u0336\u0003>\u001f\u0000\u0334\u0336\u0003"+
		"0\u0018\u0000\u0335\u0333\u0001\u0000\u0000\u0000\u0335\u0334\u0001\u0000"+
		"\u0000\u0000\u0336\u0345\u0001\u0000\u0000\u0000\u0337\u0346\u0003r9\u0000"+
		"\u0338\u033b\u0005-\u0000\u0000\u0339\u033c\u0003>\u001f\u0000\u033a\u033c"+
		"\u00030\u0018\u0000\u033b\u0339\u0001\u0000\u0000\u0000\u033b\u033a\u0001"+
		"\u0000\u0000\u0000\u033c\u033e\u0001\u0000\u0000\u0000\u033d\u0338\u0001"+
		"\u0000\u0000\u0000\u033e\u0341\u0001\u0000\u0000\u0000\u033f\u033d\u0001"+
		"\u0000\u0000\u0000\u033f\u0340\u0001\u0000\u0000\u0000\u0340\u0343\u0001"+
		"\u0000\u0000\u0000\u0341\u033f\u0001\u0000\u0000\u0000\u0342\u0344\u0005"+
		"-\u0000\u0000\u0343\u0342\u0001\u0000\u0000\u0000\u0343\u0344\u0001\u0000"+
		"\u0000\u0000\u0344\u0346\u0001\u0000\u0000\u0000\u0345\u0337\u0001\u0000"+
		"\u0000\u0000\u0345\u033f\u0001\u0000\u0000\u0000\u0346U\u0001\u0000\u0000"+
		"\u0000\u0347\u034c\u0003>\u001f\u0000\u0348\u0349\u0005-\u0000\u0000\u0349"+
		"\u034b\u0003>\u001f\u0000\u034a\u0348\u0001\u0000\u0000\u0000\u034b\u034e"+
		"\u0001\u0000\u0000\u0000\u034c\u034a\u0001\u0000\u0000\u0000\u034c\u034d"+
		"\u0001\u0000\u0000\u0000\u034d\u0350\u0001\u0000\u0000\u0000\u034e\u034c"+
		"\u0001\u0000\u0000\u0000\u034f\u0351\u0005-\u0000\u0000\u0350\u034f\u0001"+
		"\u0000\u0000\u0000\u0350\u0351\u0001\u0000\u0000\u0000\u0351W\u0001\u0000"+
		"\u0000\u0000\u0352\u0353\u0006,\uffff\uffff\u0000\u0353\u0354\u0003Z-"+
		"\u0000\u0354\u035a\u0001\u0000\u0000\u0000\u0355\u0356\n\u0002\u0000\u0000"+
		"\u0356\u0357\u0005)\u0000\u0000\u0357\u0359\u0003Z-\u0000\u0358\u0355"+
		"\u0001\u0000\u0000\u0000\u0359\u035c\u0001\u0000\u0000\u0000\u035a\u0358"+
		"\u0001\u0000\u0000\u0000\u035a\u035b\u0001\u0000\u0000\u0000\u035bY\u0001"+
		"\u0000\u0000\u0000\u035c\u035a\u0001\u0000\u0000\u0000\u035d\u035e\u0007"+
		"\u0007\u0000\u0000\u035e[\u0001\u0000\u0000\u0000\u035f\u0363\u0003^/"+
		"\u0000\u0360\u0363\u0005X\u0000\u0000\u0361\u0363\u0005Y\u0000\u0000\u0362"+
		"\u035f\u0001\u0000\u0000\u0000\u0362\u0360\u0001\u0000\u0000\u0000\u0362"+
		"\u0361\u0001\u0000\u0000\u0000\u0363]\u0001\u0000\u0000\u0000\u0364\u0365"+
		"\u0007\b\u0000\u0000\u0365_\u0001\u0000\u0000\u0000\u0366\u0368\u0005"+
		"\u001e\u0000\u0000\u0367\u0369\u0003b1\u0000\u0368\u0367\u0001\u0000\u0000"+
		"\u0000\u0368\u0369\u0001\u0000\u0000\u0000\u0369a\u0001\u0000\u0000\u0000"+
		"\u036a\u036b\u0005\u0007\u0000\u0000\u036b\u036e\u0003>\u001f\u0000\u036c"+
		"\u036e\u0003V+\u0000\u036d\u036a\u0001\u0000\u0000\u0000\u036d\u036c\u0001"+
		"\u0000\u0000\u0000\u036ec\u0001\u0000\u0000\u0000\u036f\u0370\u0005)\u0000"+
		"\u0000\u0370\u0372\u0003Z-\u0000\u0371\u0373\u0003f3\u0000\u0372\u0371"+
		"\u0001\u0000\u0000\u0000\u0372\u0373\u0001\u0000\u0000\u0000\u0373\u0376"+
		"\u0001\u0000\u0000\u0000\u0374\u0376\u0003f3\u0000\u0375\u036f\u0001\u0000"+
		"\u0000\u0000\u0375\u0374\u0001\u0000\u0000\u0000\u0376e\u0001\u0000\u0000"+
		"\u0000\u0377\u0379\u0005Z\u0000\u0000\u0378\u037a\u0003h4\u0000\u0379"+
		"\u0378\u0001\u0000\u0000\u0000\u0379\u037a\u0001\u0000\u0000\u0000\u037a"+
		"\u037b\u0001\u0000\u0000\u0000\u037b\u0381\u0005[\u0000\u0000\u037c\u037d"+
		"\u0005^\u0000\u0000\u037d\u037e\u0003l6\u0000\u037e\u037f\u0005_\u0000"+
		"\u0000\u037f\u0381\u0001\u0000\u0000\u0000\u0380\u0377\u0001\u0000\u0000"+
		"\u0000\u0380\u037c\u0001\u0000\u0000\u0000\u0381g\u0001\u0000\u0000\u0000"+
		"\u0382\u0387\u0003j5\u0000\u0383\u0384\u0005-\u0000\u0000\u0384\u0386"+
		"\u0003j5\u0000\u0385\u0383\u0001\u0000\u0000\u0000\u0386\u0389\u0001\u0000"+
		"\u0000\u0000\u0387\u0385\u0001\u0000\u0000\u0000\u0387\u0388\u0001\u0000"+
		"\u0000\u0000\u0388\u038b\u0001\u0000\u0000\u0000\u0389\u0387\u0001\u0000"+
		"\u0000\u0000\u038a\u038c\u0005-\u0000\u0000\u038b\u038a\u0001\u0000\u0000"+
		"\u0000\u038b\u038c\u0001\u0000\u0000\u0000\u038ci\u0001\u0000\u0000\u0000"+
		"\u038d\u0391\u0003>\u001f\u0000\u038e\u0392\u0003r9\u0000\u038f\u0390"+
		"\u00051\u0000\u0000\u0390\u0392\u0003>\u001f\u0000\u0391\u038e\u0001\u0000"+
		"\u0000\u0000\u0391\u038f\u0001\u0000\u0000\u0000\u0391\u0392\u0001\u0000"+
		"\u0000\u0000\u0392\u0396\u0001\u0000\u0000\u0000\u0393\u0394\u0007\t\u0000"+
		"\u0000\u0394\u0396\u0003>\u001f\u0000\u0395\u038d\u0001\u0000\u0000\u0000"+
		"\u0395\u0393\u0001\u0000\u0000\u0000\u0396k\u0001\u0000\u0000\u0000\u0397"+
		"\u039c\u0003n7\u0000\u0398\u0399\u0005-\u0000\u0000\u0399\u039b\u0003"+
		"n7\u0000\u039a\u0398\u0001\u0000\u0000\u0000\u039b\u039e\u0001\u0000\u0000"+
		"\u0000\u039c\u039a\u0001\u0000\u0000\u0000\u039c\u039d\u0001\u0000\u0000"+
		"\u0000\u039d\u03a0\u0001\u0000\u0000\u0000\u039e\u039c\u0001\u0000\u0000"+
		"\u0000\u039f\u03a1\u0005-\u0000\u0000\u03a0\u039f\u0001\u0000\u0000\u0000"+
		"\u03a0\u03a1\u0001\u0000\u0000\u0000\u03a1m\u0001\u0000\u0000\u0000\u03a2"+
		"\u03b5\u0005*\u0000\u0000\u03a3\u03ab\u0003>\u001f\u0000\u03a4\u03a6\u0005"+
		".\u0000\u0000\u03a5\u03a7\u0003>\u001f\u0000\u03a6\u03a5\u0001\u0000\u0000"+
		"\u0000\u03a6\u03a7\u0001\u0000\u0000\u0000\u03a7\u03a9\u0001\u0000\u0000"+
		"\u0000\u03a8\u03aa\u0003p8\u0000\u03a9\u03a8\u0001\u0000\u0000\u0000\u03a9"+
		"\u03aa\u0001\u0000\u0000\u0000\u03aa\u03ac\u0001\u0000\u0000\u0000\u03ab"+
		"\u03a4\u0001\u0000\u0000\u0000\u03ab\u03ac\u0001\u0000\u0000\u0000\u03ac"+
		"\u03b5\u0001\u0000\u0000\u0000\u03ad\u03af\u0005.\u0000\u0000\u03ae\u03b0"+
		"\u0003>\u001f\u0000\u03af\u03ae\u0001\u0000\u0000\u0000\u03af\u03b0\u0001"+
		"\u0000\u0000\u0000\u03b0\u03b2\u0001\u0000\u0000\u0000\u03b1\u03b3\u0003"+
		"p8\u0000\u03b2\u03b1\u0001\u0000\u0000\u0000\u03b2\u03b3\u0001\u0000\u0000"+
		"\u0000\u03b3\u03b5\u0001\u0000\u0000\u0000\u03b4\u03a2\u0001\u0000\u0000"+
		"\u0000\u03b4\u03a3\u0001\u0000\u0000\u0000\u03b4\u03ad\u0001\u0000\u0000"+
		"\u0000\u03b5o\u0001\u0000\u0000\u0000\u03b6\u03b8\u0005.\u0000\u0000\u03b7"+
		"\u03b9\u0003>\u001f\u0000\u03b8\u03b7\u0001\u0000\u0000\u0000\u03b8\u03b9"+
		"\u0001\u0000\u0000\u0000\u03b9q\u0001\u0000\u0000\u0000\u03ba\u03bb\u0005"+
		"\u0011\u0000\u0000\u03bb\u03bc\u00034\u001a\u0000\u03bc\u03bd\u0005\u0012"+
		"\u0000\u0000\u03bd\u03bf\u0003J%\u0000\u03be\u03c0\u0003t:\u0000\u03bf"+
		"\u03be\u0001\u0000\u0000\u0000\u03bf\u03c0\u0001\u0000\u0000\u0000\u03c0"+
		"s\u0001\u0000\u0000\u0000\u03c1\u03c8\u0003r9\u0000\u03c2\u03c3\u0005"+
		"\r\u0000\u0000\u03c3\u03c5\u0003>\u001f\u0000\u03c4\u03c6\u0003t:\u0000"+
		"\u03c5\u03c4\u0001\u0000\u0000\u0000\u03c5\u03c6\u0001\u0000\u0000\u0000"+
		"\u03c6\u03c8\u0001\u0000\u0000\u0000\u03c7\u03c1\u0001\u0000\u0000\u0000"+
		"\u03c7\u03c2\u0001\u0000\u0000\u0000\u03c8u\u0001\u0000\u0000\u0000\u0097"+
		"y\u0082\u0086\u0088\u008e\u0093\u009c\u00a0\u00a7\u00aa\u00b3\u00bb\u00be"+
		"\u00c1\u00c4\u00c7\u00cf\u00d8\u00dd\u00df\u00e7\u00eb\u00f1\u00f4\u0108"+
		"\u0116\u0118\u0121\u0124\u012a\u0130\u0135\u013d\u0142\u0146\u0149\u014c"+
		"\u0150\u0152\u015f\u0165\u0168\u016d\u0174\u0178\u017e\u0187\u018b\u0193"+
		"\u0196\u0198\u01a3\u01ab\u01ad\u01af\u01b3\u01bc\u01c3\u01c5\u01ce\u01d6"+
		"\u01e0\u01e2\u01ea\u01f3\u01f8\u01fc\u0202\u0206\u0209\u0214\u0219\u021c"+
		"\u0223\u022a\u022c\u0233\u0237\u023e\u0242\u0247\u024e\u0254\u025c\u0260"+
		"\u0264\u0269\u026e\u0272\u0275\u0278\u027c\u027e\u0285\u028b\u028e\u029a"+
		"\u02a2\u02a4\u02b3\u02b8\u02ba\u02bf\u02c4\u02ca\u02cf\u02e6\u02e8\u02ee"+
		"\u02f3\u02f8\u02fe\u0307\u030e\u0310\u0318\u0321\u0325\u0329\u0331\u0335"+
		"\u033b\u033f\u0343\u0345\u034c\u0350\u035a\u0362\u0368\u036d\u0372\u0375"+
		"\u0379\u0380\u0387\u038b\u0391\u0395\u039c\u03a0\u03a6\u03a9\u03ab\u03af"+
		"\u03b2\u03b4\u03b8\u03bf\u03c5\u03c7";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}