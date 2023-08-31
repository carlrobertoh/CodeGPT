// Generated from TypeScriptParser.g4 by ANTLR 4.13.0
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
public class TypeScriptParser extends TypeScriptParserBase {
	static { RuntimeMetaData.checkVersion("4.13.0", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		MultiLineComment=1, SingleLineComment=2, RegularExpressionLiteral=3, OpenBracket=4, 
		CloseBracket=5, OpenParen=6, CloseParen=7, OpenBrace=8, TemplateCloseBrace=9, 
		CloseBrace=10, SemiColon=11, Comma=12, Assign=13, QuestionMark=14, Colon=15, 
		Ellipsis=16, Dot=17, PlusPlus=18, MinusMinus=19, Plus=20, Minus=21, BitNot=22, 
		Not=23, Multiply=24, Divide=25, Modulus=26, RightShiftArithmetic=27, LeftShiftArithmetic=28, 
		RightShiftLogical=29, LessThan=30, MoreThan=31, LessThanEquals=32, GreaterThanEquals=33, 
		Equals_=34, NotEquals=35, IdentityEquals=36, IdentityNotEquals=37, BitAnd=38, 
		BitXOr=39, BitOr=40, And=41, Or=42, MultiplyAssign=43, DivideAssign=44, 
		ModulusAssign=45, PlusAssign=46, MinusAssign=47, LeftShiftArithmeticAssign=48, 
		RightShiftArithmeticAssign=49, RightShiftLogicalAssign=50, BitAndAssign=51, 
		BitXorAssign=52, BitOrAssign=53, ARROW=54, NullLiteral=55, BooleanLiteral=56, 
		DecimalLiteral=57, HexIntegerLiteral=58, OctalIntegerLiteral=59, OctalIntegerLiteral2=60, 
		BinaryIntegerLiteral=61, Break=62, Do=63, Instanceof=64, Typeof=65, Case=66, 
		Else=67, New=68, Var=69, Catch=70, Finally=71, Return=72, Void=73, Continue=74, 
		For=75, Switch=76, While=77, Debugger=78, Function_=79, This=80, With=81, 
		Default=82, If=83, Throw=84, Delete=85, In=86, Try=87, As=88, From=89, 
		ReadOnly=90, Async=91, Class=92, Enum=93, Extends=94, Super=95, Const=96, 
		Export=97, Import=98, Implements=99, Let=100, Private=101, Public=102, 
		Interface=103, Package=104, Protected=105, Static=106, Yield=107, Any=108, 
		Number=109, Boolean=110, String=111, Symbol=112, TypeAlias=113, Get=114, 
		Set=115, Constructor=116, Namespace=117, Require=118, Module=119, Declare=120, 
		Abstract=121, Is=122, At=123, Identifier=124, StringLiteral=125, BackTick=126, 
		WhiteSpaces=127, LineTerminator=128, HtmlComment=129, CDataComment=130, 
		UnexpectedCharacter=131, TemplateStringEscapeAtom=132, TemplateStringStartExpression=133, 
		TemplateStringAtom=134;
	public static final int
		RULE_initializer = 0, RULE_bindingPattern = 1, RULE_typeParameters = 2, 
		RULE_typeParameterList = 3, RULE_typeParameter = 4, RULE_constraint = 5, 
		RULE_typeArguments = 6, RULE_typeArgumentList = 7, RULE_typeArgument = 8, 
		RULE_type_ = 9, RULE_unionOrIntersectionOrPrimaryType = 10, RULE_primaryType = 11, 
		RULE_predefinedType = 12, RULE_typeReference = 13, RULE_nestedTypeGeneric = 14, 
		RULE_typeGeneric = 15, RULE_typeIncludeGeneric = 16, RULE_typeName = 17, 
		RULE_objectType = 18, RULE_typeBody = 19, RULE_typeMemberList = 20, RULE_typeMember = 21, 
		RULE_arrayType = 22, RULE_tupleType = 23, RULE_tupleElementTypes = 24, 
		RULE_functionType = 25, RULE_constructorType = 26, RULE_typeQuery = 27, 
		RULE_typeQueryExpression = 28, RULE_propertySignatur = 29, RULE_typeAnnotation = 30, 
		RULE_callSignature = 31, RULE_parameterList = 32, RULE_requiredParameterList = 33, 
		RULE_parameter = 34, RULE_optionalParameter = 35, RULE_restParameter = 36, 
		RULE_requiredParameter = 37, RULE_accessibilityModifier = 38, RULE_identifierOrPattern = 39, 
		RULE_constructSignature = 40, RULE_indexSignature = 41, RULE_methodSignature = 42, 
		RULE_typeAliasDeclaration = 43, RULE_constructorDeclaration = 44, RULE_interfaceDeclaration = 45, 
		RULE_interfaceExtendsClause = 46, RULE_classOrInterfaceTypeList = 47, 
		RULE_enumDeclaration = 48, RULE_enumBody = 49, RULE_enumMemberList = 50, 
		RULE_enumMember = 51, RULE_namespaceDeclaration = 52, RULE_namespaceName = 53, 
		RULE_importAliasDeclaration = 54, RULE_decoratorList = 55, RULE_decorator = 56, 
		RULE_decoratorMemberExpression = 57, RULE_decoratorCallExpression = 58, 
		RULE_program = 59, RULE_sourceElement = 60, RULE_statement = 61, RULE_block = 62, 
		RULE_statementList = 63, RULE_abstractDeclaration = 64, RULE_importStatement = 65, 
		RULE_fromBlock = 66, RULE_multipleImportStatement = 67, RULE_exportStatement = 68, 
		RULE_variableStatement = 69, RULE_variableDeclarationList = 70, RULE_variableDeclaration = 71, 
		RULE_emptyStatement_ = 72, RULE_expressionStatement = 73, RULE_ifStatement = 74, 
		RULE_iterationStatement = 75, RULE_varModifier = 76, RULE_continueStatement = 77, 
		RULE_breakStatement = 78, RULE_returnStatement = 79, RULE_yieldStatement = 80, 
		RULE_withStatement = 81, RULE_switchStatement = 82, RULE_caseBlock = 83, 
		RULE_caseClauses = 84, RULE_caseClause = 85, RULE_defaultClause = 86, 
		RULE_labelledStatement = 87, RULE_throwStatement = 88, RULE_tryStatement = 89, 
		RULE_catchProduction = 90, RULE_finallyProduction = 91, RULE_debuggerStatement = 92, 
		RULE_functionDeclaration = 93, RULE_classDeclaration = 94, RULE_classHeritage = 95, 
		RULE_classTail = 96, RULE_classExtendsClause = 97, RULE_implementsClause = 98, 
		RULE_classElement = 99, RULE_propertyMemberDeclaration = 100, RULE_propertyMemberBase = 101, 
		RULE_indexMemberDeclaration = 102, RULE_generatorMethod = 103, RULE_generatorFunctionDeclaration = 104, 
		RULE_generatorBlock = 105, RULE_generatorDefinition = 106, RULE_iteratorBlock = 107, 
		RULE_iteratorDefinition = 108, RULE_formalParameterList = 109, RULE_formalParameterArg = 110, 
		RULE_lastFormalParameterArg = 111, RULE_functionBody = 112, RULE_sourceElements = 113, 
		RULE_arrayLiteral = 114, RULE_elementList = 115, RULE_arrayElement = 116, 
		RULE_objectLiteral = 117, RULE_propertyAssignment = 118, RULE_getAccessor = 119, 
		RULE_setAccessor = 120, RULE_propertyName = 121, RULE_arguments = 122, 
		RULE_argumentList = 123, RULE_argument = 124, RULE_expressionSequence = 125, 
		RULE_functionExpressionDeclaration = 126, RULE_singleExpression = 127, 
		RULE_asExpression = 128, RULE_arrowFunctionDeclaration = 129, RULE_arrowFunctionParameters = 130, 
		RULE_arrowFunctionBody = 131, RULE_assignmentOperator = 132, RULE_literal = 133, 
		RULE_templateStringLiteral = 134, RULE_templateStringAtom = 135, RULE_numericLiteral = 136, 
		RULE_identifierName = 137, RULE_identifierOrKeyWord = 138, RULE_reservedWord = 139, 
		RULE_keyword = 140, RULE_getter = 141, RULE_setter = 142, RULE_eos = 143;
	private static String[] makeRuleNames() {
		return new String[] {
			"initializer", "bindingPattern", "typeParameters", "typeParameterList", 
			"typeParameter", "constraint", "typeArguments", "typeArgumentList", "typeArgument", 
			"type_", "unionOrIntersectionOrPrimaryType", "primaryType", "predefinedType", 
			"typeReference", "nestedTypeGeneric", "typeGeneric", "typeIncludeGeneric", 
			"typeName", "objectType", "typeBody", "typeMemberList", "typeMember", 
			"arrayType", "tupleType", "tupleElementTypes", "functionType", "constructorType", 
			"typeQuery", "typeQueryExpression", "propertySignatur", "typeAnnotation", 
			"callSignature", "parameterList", "requiredParameterList", "parameter", 
			"optionalParameter", "restParameter", "requiredParameter", "accessibilityModifier", 
			"identifierOrPattern", "constructSignature", "indexSignature", "methodSignature", 
			"typeAliasDeclaration", "constructorDeclaration", "interfaceDeclaration", 
			"interfaceExtendsClause", "classOrInterfaceTypeList", "enumDeclaration", 
			"enumBody", "enumMemberList", "enumMember", "namespaceDeclaration", "namespaceName", 
			"importAliasDeclaration", "decoratorList", "decorator", "decoratorMemberExpression", 
			"decoratorCallExpression", "program", "sourceElement", "statement", "block", 
			"statementList", "abstractDeclaration", "importStatement", "fromBlock", 
			"multipleImportStatement", "exportStatement", "variableStatement", "variableDeclarationList", 
			"variableDeclaration", "emptyStatement_", "expressionStatement", "ifStatement", 
			"iterationStatement", "varModifier", "continueStatement", "breakStatement", 
			"returnStatement", "yieldStatement", "withStatement", "switchStatement", 
			"caseBlock", "caseClauses", "caseClause", "defaultClause", "labelledStatement", 
			"throwStatement", "tryStatement", "catchProduction", "finallyProduction", 
			"debuggerStatement", "functionDeclaration", "classDeclaration", "classHeritage", 
			"classTail", "classExtendsClause", "implementsClause", "classElement", 
			"propertyMemberDeclaration", "propertyMemberBase", "indexMemberDeclaration", 
			"generatorMethod", "generatorFunctionDeclaration", "generatorBlock", 
			"generatorDefinition", "iteratorBlock", "iteratorDefinition", "formalParameterList", 
			"formalParameterArg", "lastFormalParameterArg", "functionBody", "sourceElements", 
			"arrayLiteral", "elementList", "arrayElement", "objectLiteral", "propertyAssignment", 
			"getAccessor", "setAccessor", "propertyName", "arguments", "argumentList", 
			"argument", "expressionSequence", "functionExpressionDeclaration", "singleExpression", 
			"asExpression", "arrowFunctionDeclaration", "arrowFunctionParameters", 
			"arrowFunctionBody", "assignmentOperator", "literal", "templateStringLiteral", 
			"templateStringAtom", "numericLiteral", "identifierName", "identifierOrKeyWord", 
			"reservedWord", "keyword", "getter", "setter", "eos"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, "'['", "']'", "'('", "')'", "'{'", null, "'}'", 
			"';'", "','", "'='", "'?'", "':'", "'...'", "'.'", "'++'", "'--'", "'+'", 
			"'-'", "'~'", "'!'", "'*'", "'/'", "'%'", "'>>'", "'<<'", "'>>>'", "'<'", 
			"'>'", "'<='", "'>='", "'=='", "'!='", "'==='", "'!=='", "'&'", "'^'", 
			"'|'", "'&&'", "'||'", "'*='", "'/='", "'%='", "'+='", "'-='", "'<<='", 
			"'>>='", "'>>>='", "'&='", "'^='", "'|='", "'=>'", "'null'", null, null, 
			null, null, null, null, "'break'", "'do'", "'instanceof'", "'typeof'", 
			"'case'", "'else'", "'new'", "'var'", "'catch'", "'finally'", "'return'", 
			"'void'", "'continue'", "'for'", "'switch'", "'while'", "'debugger'", 
			"'function'", "'this'", "'with'", "'default'", "'if'", "'throw'", "'delete'", 
			"'in'", "'try'", "'as'", "'from'", "'readonly'", "'async'", "'class'", 
			"'enum'", "'extends'", "'super'", "'const'", "'export'", "'import'", 
			"'implements'", "'let'", "'private'", "'public'", "'interface'", "'package'", 
			"'protected'", "'static'", "'yield'", "'any'", "'number'", "'boolean'", 
			"'string'", "'symbol'", "'type'", "'get'", "'set'", "'constructor'", 
			"'namespace'", "'require'", "'module'", "'declare'", "'abstract'", "'is'", 
			"'@'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "MultiLineComment", "SingleLineComment", "RegularExpressionLiteral", 
			"OpenBracket", "CloseBracket", "OpenParen", "CloseParen", "OpenBrace", 
			"TemplateCloseBrace", "CloseBrace", "SemiColon", "Comma", "Assign", "QuestionMark", 
			"Colon", "Ellipsis", "Dot", "PlusPlus", "MinusMinus", "Plus", "Minus", 
			"BitNot", "Not", "Multiply", "Divide", "Modulus", "RightShiftArithmetic", 
			"LeftShiftArithmetic", "RightShiftLogical", "LessThan", "MoreThan", "LessThanEquals", 
			"GreaterThanEquals", "Equals_", "NotEquals", "IdentityEquals", "IdentityNotEquals", 
			"BitAnd", "BitXOr", "BitOr", "And", "Or", "MultiplyAssign", "DivideAssign", 
			"ModulusAssign", "PlusAssign", "MinusAssign", "LeftShiftArithmeticAssign", 
			"RightShiftArithmeticAssign", "RightShiftLogicalAssign", "BitAndAssign", 
			"BitXorAssign", "BitOrAssign", "ARROW", "NullLiteral", "BooleanLiteral", 
			"DecimalLiteral", "HexIntegerLiteral", "OctalIntegerLiteral", "OctalIntegerLiteral2", 
			"BinaryIntegerLiteral", "Break", "Do", "Instanceof", "Typeof", "Case", 
			"Else", "New", "Var", "Catch", "Finally", "Return", "Void", "Continue", 
			"For", "Switch", "While", "Debugger", "Function_", "This", "With", "Default", 
			"If", "Throw", "Delete", "In", "Try", "As", "From", "ReadOnly", "Async", 
			"Class", "Enum", "Extends", "Super", "Const", "Export", "Import", "Implements", 
			"Let", "Private", "Public", "Interface", "Package", "Protected", "Static", 
			"Yield", "Any", "Number", "Boolean", "String", "Symbol", "TypeAlias", 
			"Get", "Set", "Constructor", "Namespace", "Require", "Module", "Declare", 
			"Abstract", "Is", "At", "Identifier", "StringLiteral", "BackTick", "WhiteSpaces", 
			"LineTerminator", "HtmlComment", "CDataComment", "UnexpectedCharacter", 
			"TemplateStringEscapeAtom", "TemplateStringStartExpression", "TemplateStringAtom"
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
	public String getGrammarFileName() { return "TypeScriptParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TypeScriptParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InitializerContext extends ParserRuleContext {
		public TerminalNode Assign() { return getToken(TypeScriptParser.Assign, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public InitializerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_initializer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterInitializer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitInitializer(this);
		}
	}

	public final InitializerContext initializer() throws RecognitionException {
		InitializerContext _localctx = new InitializerContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_initializer);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(288);
			match(Assign);
			setState(289);
			singleExpression(0);
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
	public static class BindingPatternContext extends ParserRuleContext {
		public ArrayLiteralContext arrayLiteral() {
			return getRuleContext(ArrayLiteralContext.class,0);
		}
		public ObjectLiteralContext objectLiteral() {
			return getRuleContext(ObjectLiteralContext.class,0);
		}
		public BindingPatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bindingPattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterBindingPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitBindingPattern(this);
		}
	}

	public final BindingPatternContext bindingPattern() throws RecognitionException {
		BindingPatternContext _localctx = new BindingPatternContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_bindingPattern);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(293);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OpenBracket:
				{
				setState(291);
				arrayLiteral();
				}
				break;
			case OpenBrace:
				{
				setState(292);
				objectLiteral();
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
	public static class TypeParametersContext extends ParserRuleContext {
		public TerminalNode LessThan() { return getToken(TypeScriptParser.LessThan, 0); }
		public TerminalNode MoreThan() { return getToken(TypeScriptParser.MoreThan, 0); }
		public TypeParameterListContext typeParameterList() {
			return getRuleContext(TypeParameterListContext.class,0);
		}
		public TypeParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeParameters(this);
		}
	}

	public final TypeParametersContext typeParameters() throws RecognitionException {
		TypeParametersContext _localctx = new TypeParametersContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_typeParameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(295);
			match(LessThan);
			setState(297);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LessThan || _la==Identifier) {
				{
				setState(296);
				typeParameterList();
				}
			}

			setState(299);
			match(MoreThan);
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
	public static class TypeParameterListContext extends ParserRuleContext {
		public List<TypeParameterContext> typeParameter() {
			return getRuleContexts(TypeParameterContext.class);
		}
		public TypeParameterContext typeParameter(int i) {
			return getRuleContext(TypeParameterContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public TypeParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeParameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeParameterList(this);
		}
	}

	public final TypeParameterListContext typeParameterList() throws RecognitionException {
		TypeParameterListContext _localctx = new TypeParameterListContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_typeParameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(301);
			typeParameter();
			setState(306);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(302);
				match(Comma);
				setState(303);
				typeParameter();
				}
				}
				setState(308);
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
	public static class TypeParameterContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public ConstraintContext constraint() {
			return getRuleContext(ConstraintContext.class,0);
		}
		public TypeParametersContext typeParameters() {
			return getRuleContext(TypeParametersContext.class,0);
		}
		public TypeParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeParameter(this);
		}
	}

	public final TypeParameterContext typeParameter() throws RecognitionException {
		TypeParameterContext _localctx = new TypeParameterContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_typeParameter);
		int _la;
		try {
			setState(314);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(309);
				match(Identifier);
				setState(311);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Extends) {
					{
					setState(310);
					constraint();
					}
				}

				}
				break;
			case LessThan:
				enterOuterAlt(_localctx, 2);
				{
				setState(313);
				typeParameters();
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
	public static class ConstraintContext extends ParserRuleContext {
		public TerminalNode Extends() { return getToken(TypeScriptParser.Extends, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public ConstraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constraint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterConstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitConstraint(this);
		}
	}

	public final ConstraintContext constraint() throws RecognitionException {
		ConstraintContext _localctx = new ConstraintContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_constraint);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(316);
			match(Extends);
			setState(317);
			type_();
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
	public static class TypeArgumentsContext extends ParserRuleContext {
		public TerminalNode LessThan() { return getToken(TypeScriptParser.LessThan, 0); }
		public TerminalNode MoreThan() { return getToken(TypeScriptParser.MoreThan, 0); }
		public TypeArgumentListContext typeArgumentList() {
			return getRuleContext(TypeArgumentListContext.class,0);
		}
		public TypeArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeArguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeArguments(this);
		}
	}

	public final TypeArgumentsContext typeArguments() throws RecognitionException {
		TypeArgumentsContext _localctx = new TypeArgumentsContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_typeArguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(319);
			match(LessThan);
			setState(321);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1073742160L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 1729654935793991945L) != 0)) {
				{
				setState(320);
				typeArgumentList();
				}
			}

			setState(323);
			match(MoreThan);
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
	public static class TypeArgumentListContext extends ParserRuleContext {
		public List<TypeArgumentContext> typeArgument() {
			return getRuleContexts(TypeArgumentContext.class);
		}
		public TypeArgumentContext typeArgument(int i) {
			return getRuleContext(TypeArgumentContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public TypeArgumentListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeArgumentList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeArgumentList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeArgumentList(this);
		}
	}

	public final TypeArgumentListContext typeArgumentList() throws RecognitionException {
		TypeArgumentListContext _localctx = new TypeArgumentListContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_typeArgumentList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(325);
			typeArgument();
			setState(330);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(326);
				match(Comma);
				setState(327);
				typeArgument();
				}
				}
				setState(332);
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
	public static class TypeArgumentContext extends ParserRuleContext {
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TypeArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeArgument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeArgument(this);
		}
	}

	public final TypeArgumentContext typeArgument() throws RecognitionException {
		TypeArgumentContext _localctx = new TypeArgumentContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_typeArgument);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(333);
			type_();
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
	public static class Type_Context extends ParserRuleContext {
		public UnionOrIntersectionOrPrimaryTypeContext unionOrIntersectionOrPrimaryType() {
			return getRuleContext(UnionOrIntersectionOrPrimaryTypeContext.class,0);
		}
		public FunctionTypeContext functionType() {
			return getRuleContext(FunctionTypeContext.class,0);
		}
		public ConstructorTypeContext constructorType() {
			return getRuleContext(ConstructorTypeContext.class,0);
		}
		public TypeGenericContext typeGeneric() {
			return getRuleContext(TypeGenericContext.class,0);
		}
		public TerminalNode StringLiteral() { return getToken(TypeScriptParser.StringLiteral, 0); }
		public Type_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterType_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitType_(this);
		}
	}

	public final Type_Context type_() throws RecognitionException {
		Type_Context _localctx = new Type_Context(_ctx, getState());
		enterRule(_localctx, 18, RULE_type_);
		try {
			setState(340);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(335);
				unionOrIntersectionOrPrimaryType(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(336);
				functionType();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(337);
				constructorType();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(338);
				typeGeneric();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(339);
				match(StringLiteral);
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
	public static class UnionOrIntersectionOrPrimaryTypeContext extends ParserRuleContext {
		public UnionOrIntersectionOrPrimaryTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unionOrIntersectionOrPrimaryType; }
	 
		public UnionOrIntersectionOrPrimaryTypeContext() { }
		public void copyFrom(UnionOrIntersectionOrPrimaryTypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IntersectionContext extends UnionOrIntersectionOrPrimaryTypeContext {
		public List<UnionOrIntersectionOrPrimaryTypeContext> unionOrIntersectionOrPrimaryType() {
			return getRuleContexts(UnionOrIntersectionOrPrimaryTypeContext.class);
		}
		public UnionOrIntersectionOrPrimaryTypeContext unionOrIntersectionOrPrimaryType(int i) {
			return getRuleContext(UnionOrIntersectionOrPrimaryTypeContext.class,i);
		}
		public TerminalNode BitAnd() { return getToken(TypeScriptParser.BitAnd, 0); }
		public IntersectionContext(UnionOrIntersectionOrPrimaryTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterIntersection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitIntersection(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrimaryContext extends UnionOrIntersectionOrPrimaryTypeContext {
		public PrimaryTypeContext primaryType() {
			return getRuleContext(PrimaryTypeContext.class,0);
		}
		public PrimaryContext(UnionOrIntersectionOrPrimaryTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitPrimary(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class UnionContext extends UnionOrIntersectionOrPrimaryTypeContext {
		public List<UnionOrIntersectionOrPrimaryTypeContext> unionOrIntersectionOrPrimaryType() {
			return getRuleContexts(UnionOrIntersectionOrPrimaryTypeContext.class);
		}
		public UnionOrIntersectionOrPrimaryTypeContext unionOrIntersectionOrPrimaryType(int i) {
			return getRuleContext(UnionOrIntersectionOrPrimaryTypeContext.class,i);
		}
		public TerminalNode BitOr() { return getToken(TypeScriptParser.BitOr, 0); }
		public UnionContext(UnionOrIntersectionOrPrimaryTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterUnion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitUnion(this);
		}
	}

	public final UnionOrIntersectionOrPrimaryTypeContext unionOrIntersectionOrPrimaryType() throws RecognitionException {
		return unionOrIntersectionOrPrimaryType(0);
	}

	private UnionOrIntersectionOrPrimaryTypeContext unionOrIntersectionOrPrimaryType(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		UnionOrIntersectionOrPrimaryTypeContext _localctx = new UnionOrIntersectionOrPrimaryTypeContext(_ctx, _parentState);
		UnionOrIntersectionOrPrimaryTypeContext _prevctx = _localctx;
		int _startState = 20;
		enterRecursionRule(_localctx, 20, RULE_unionOrIntersectionOrPrimaryType, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new PrimaryContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(343);
			primaryType(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(353);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(351);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
					case 1:
						{
						_localctx = new UnionContext(new UnionOrIntersectionOrPrimaryTypeContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_unionOrIntersectionOrPrimaryType);
						setState(345);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(346);
						match(BitOr);
						setState(347);
						unionOrIntersectionOrPrimaryType(4);
						}
						break;
					case 2:
						{
						_localctx = new IntersectionContext(new UnionOrIntersectionOrPrimaryTypeContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_unionOrIntersectionOrPrimaryType);
						setState(348);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(349);
						match(BitAnd);
						setState(350);
						unionOrIntersectionOrPrimaryType(3);
						}
						break;
					}
					} 
				}
				setState(355);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
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
	public static class PrimaryTypeContext extends ParserRuleContext {
		public PrimaryTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryType; }
	 
		public PrimaryTypeContext() { }
		public void copyFrom(PrimaryTypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RedefinitionOfTypeContext extends PrimaryTypeContext {
		public TypeReferenceContext typeReference() {
			return getRuleContext(TypeReferenceContext.class,0);
		}
		public TerminalNode Is() { return getToken(TypeScriptParser.Is, 0); }
		public PrimaryTypeContext primaryType() {
			return getRuleContext(PrimaryTypeContext.class,0);
		}
		public RedefinitionOfTypeContext(PrimaryTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterRedefinitionOfType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitRedefinitionOfType(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PredefinedPrimTypeContext extends PrimaryTypeContext {
		public PredefinedTypeContext predefinedType() {
			return getRuleContext(PredefinedTypeContext.class,0);
		}
		public PredefinedPrimTypeContext(PrimaryTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterPredefinedPrimType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitPredefinedPrimType(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ArrayPrimTypeContext extends PrimaryTypeContext {
		public PrimaryTypeContext primaryType() {
			return getRuleContext(PrimaryTypeContext.class,0);
		}
		public TerminalNode OpenBracket() { return getToken(TypeScriptParser.OpenBracket, 0); }
		public TerminalNode CloseBracket() { return getToken(TypeScriptParser.CloseBracket, 0); }
		public ArrayPrimTypeContext(PrimaryTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterArrayPrimType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitArrayPrimType(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParenthesizedPrimTypeContext extends PrimaryTypeContext {
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public ParenthesizedPrimTypeContext(PrimaryTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterParenthesizedPrimType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitParenthesizedPrimType(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ThisPrimTypeContext extends PrimaryTypeContext {
		public TerminalNode This() { return getToken(TypeScriptParser.This, 0); }
		public ThisPrimTypeContext(PrimaryTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterThisPrimType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitThisPrimType(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TuplePrimTypeContext extends PrimaryTypeContext {
		public TerminalNode OpenBracket() { return getToken(TypeScriptParser.OpenBracket, 0); }
		public TupleElementTypesContext tupleElementTypes() {
			return getRuleContext(TupleElementTypesContext.class,0);
		}
		public TerminalNode CloseBracket() { return getToken(TypeScriptParser.CloseBracket, 0); }
		public TuplePrimTypeContext(PrimaryTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTuplePrimType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTuplePrimType(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ObjectPrimTypeContext extends PrimaryTypeContext {
		public ObjectTypeContext objectType() {
			return getRuleContext(ObjectTypeContext.class,0);
		}
		public ObjectPrimTypeContext(PrimaryTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterObjectPrimType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitObjectPrimType(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ReferencePrimTypeContext extends PrimaryTypeContext {
		public TypeReferenceContext typeReference() {
			return getRuleContext(TypeReferenceContext.class,0);
		}
		public ReferencePrimTypeContext(PrimaryTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterReferencePrimType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitReferencePrimType(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class QueryPrimTypeContext extends PrimaryTypeContext {
		public TypeQueryContext typeQuery() {
			return getRuleContext(TypeQueryContext.class,0);
		}
		public QueryPrimTypeContext(PrimaryTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterQueryPrimType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitQueryPrimType(this);
		}
	}

	public final PrimaryTypeContext primaryType() throws RecognitionException {
		return primaryType(0);
	}

	private PrimaryTypeContext primaryType(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		PrimaryTypeContext _localctx = new PrimaryTypeContext(_ctx, _parentState);
		PrimaryTypeContext _prevctx = _localctx;
		int _startState = 22;
		enterRecursionRule(_localctx, 22, RULE_primaryType, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(374);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				_localctx = new ParenthesizedPrimTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(357);
				match(OpenParen);
				setState(358);
				type_();
				setState(359);
				match(CloseParen);
				}
				break;
			case 2:
				{
				_localctx = new PredefinedPrimTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(361);
				predefinedType();
				}
				break;
			case 3:
				{
				_localctx = new ReferencePrimTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(362);
				typeReference();
				}
				break;
			case 4:
				{
				_localctx = new ObjectPrimTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(363);
				objectType();
				}
				break;
			case 5:
				{
				_localctx = new TuplePrimTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(364);
				match(OpenBracket);
				setState(365);
				tupleElementTypes();
				setState(366);
				match(CloseBracket);
				}
				break;
			case 6:
				{
				_localctx = new QueryPrimTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(368);
				typeQuery();
				}
				break;
			case 7:
				{
				_localctx = new ThisPrimTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(369);
				match(This);
				}
				break;
			case 8:
				{
				_localctx = new RedefinitionOfTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(370);
				typeReference();
				setState(371);
				match(Is);
				setState(372);
				primaryType(1);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(382);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ArrayPrimTypeContext(new PrimaryTypeContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_primaryType);
					setState(376);
					if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
					setState(377);
					if (!(notLineTerminator())) throw new FailedPredicateException(this, "notLineTerminator()");
					setState(378);
					match(OpenBracket);
					setState(379);
					match(CloseBracket);
					}
					} 
				}
				setState(384);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
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
	public static class PredefinedTypeContext extends ParserRuleContext {
		public TerminalNode Any() { return getToken(TypeScriptParser.Any, 0); }
		public TerminalNode Number() { return getToken(TypeScriptParser.Number, 0); }
		public TerminalNode Boolean() { return getToken(TypeScriptParser.Boolean, 0); }
		public TerminalNode String() { return getToken(TypeScriptParser.String, 0); }
		public TerminalNode Symbol() { return getToken(TypeScriptParser.Symbol, 0); }
		public TerminalNode Void() { return getToken(TypeScriptParser.Void, 0); }
		public PredefinedTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predefinedType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterPredefinedType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitPredefinedType(this);
		}
	}

	public final PredefinedTypeContext predefinedType() throws RecognitionException {
		PredefinedTypeContext _localctx = new PredefinedTypeContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_predefinedType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(385);
			_la = _input.LA(1);
			if ( !(((((_la - 73)) & ~0x3f) == 0 && ((1L << (_la - 73)) & 1065151889409L) != 0)) ) {
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
	public static class TypeReferenceContext extends ParserRuleContext {
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public NestedTypeGenericContext nestedTypeGeneric() {
			return getRuleContext(NestedTypeGenericContext.class,0);
		}
		public TypeReferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeReference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeReference(this);
		}
	}

	public final TypeReferenceContext typeReference() throws RecognitionException {
		TypeReferenceContext _localctx = new TypeReferenceContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_typeReference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(387);
			typeName();
			setState(389);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(388);
				nestedTypeGeneric();
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
	public static class NestedTypeGenericContext extends ParserRuleContext {
		public TypeIncludeGenericContext typeIncludeGeneric() {
			return getRuleContext(TypeIncludeGenericContext.class,0);
		}
		public TypeGenericContext typeGeneric() {
			return getRuleContext(TypeGenericContext.class,0);
		}
		public NestedTypeGenericContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nestedTypeGeneric; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterNestedTypeGeneric(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitNestedTypeGeneric(this);
		}
	}

	public final NestedTypeGenericContext nestedTypeGeneric() throws RecognitionException {
		NestedTypeGenericContext _localctx = new NestedTypeGenericContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_nestedTypeGeneric);
		try {
			setState(393);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(391);
				typeIncludeGeneric();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(392);
				typeGeneric();
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
	public static class TypeGenericContext extends ParserRuleContext {
		public TerminalNode LessThan() { return getToken(TypeScriptParser.LessThan, 0); }
		public TypeArgumentListContext typeArgumentList() {
			return getRuleContext(TypeArgumentListContext.class,0);
		}
		public TerminalNode MoreThan() { return getToken(TypeScriptParser.MoreThan, 0); }
		public TypeGenericContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeGeneric; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeGeneric(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeGeneric(this);
		}
	}

	public final TypeGenericContext typeGeneric() throws RecognitionException {
		TypeGenericContext _localctx = new TypeGenericContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_typeGeneric);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(395);
			match(LessThan);
			setState(396);
			typeArgumentList();
			setState(397);
			match(MoreThan);
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
	public static class TypeIncludeGenericContext extends ParserRuleContext {
		public List<TerminalNode> LessThan() { return getTokens(TypeScriptParser.LessThan); }
		public TerminalNode LessThan(int i) {
			return getToken(TypeScriptParser.LessThan, i);
		}
		public List<TypeArgumentListContext> typeArgumentList() {
			return getRuleContexts(TypeArgumentListContext.class);
		}
		public TypeArgumentListContext typeArgumentList(int i) {
			return getRuleContext(TypeArgumentListContext.class,i);
		}
		public List<TerminalNode> MoreThan() { return getTokens(TypeScriptParser.MoreThan); }
		public TerminalNode MoreThan(int i) {
			return getToken(TypeScriptParser.MoreThan, i);
		}
		public BindingPatternContext bindingPattern() {
			return getRuleContext(BindingPatternContext.class,0);
		}
		public TerminalNode RightShiftArithmetic() { return getToken(TypeScriptParser.RightShiftArithmetic, 0); }
		public TypeIncludeGenericContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeIncludeGeneric; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeIncludeGeneric(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeIncludeGeneric(this);
		}
	}

	public final TypeIncludeGenericContext typeIncludeGeneric() throws RecognitionException {
		TypeIncludeGenericContext _localctx = new TypeIncludeGenericContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_typeIncludeGeneric);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(399);
			match(LessThan);
			setState(400);
			typeArgumentList();
			setState(401);
			match(LessThan);
			setState(402);
			typeArgumentList();
			setState(408);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MoreThan:
				{
				setState(403);
				match(MoreThan);
				setState(404);
				bindingPattern();
				setState(405);
				match(MoreThan);
				}
				break;
			case RightShiftArithmetic:
				{
				setState(407);
				match(RightShiftArithmetic);
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
	public static class TypeNameContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public NamespaceNameContext namespaceName() {
			return getRuleContext(NamespaceNameContext.class,0);
		}
		public TypeNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeName(this);
		}
	}

	public final TypeNameContext typeName() throws RecognitionException {
		TypeNameContext _localctx = new TypeNameContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_typeName);
		try {
			setState(412);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(410);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(411);
				namespaceName();
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
	public static class ObjectTypeContext extends ParserRuleContext {
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public TypeBodyContext typeBody() {
			return getRuleContext(TypeBodyContext.class,0);
		}
		public ObjectTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterObjectType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitObjectType(this);
		}
	}

	public final ObjectTypeContext objectType() throws RecognitionException {
		ObjectTypeContext _localctx = new ObjectTypeContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_objectType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(414);
			match(OpenBrace);
			setState(416);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -36028795945222064L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 3517012241796825087L) != 0)) {
				{
				setState(415);
				typeBody();
				}
			}

			setState(418);
			match(CloseBrace);
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
	public static class TypeBodyContext extends ParserRuleContext {
		public TypeMemberListContext typeMemberList() {
			return getRuleContext(TypeMemberListContext.class,0);
		}
		public TerminalNode SemiColon() { return getToken(TypeScriptParser.SemiColon, 0); }
		public TerminalNode Comma() { return getToken(TypeScriptParser.Comma, 0); }
		public TypeBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeBody(this);
		}
	}

	public final TypeBodyContext typeBody() throws RecognitionException {
		TypeBodyContext _localctx = new TypeBodyContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_typeBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(420);
			typeMemberList();
			setState(422);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SemiColon || _la==Comma) {
				{
				setState(421);
				_la = _input.LA(1);
				if ( !(_la==SemiColon || _la==Comma) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
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
	public static class TypeMemberListContext extends ParserRuleContext {
		public List<TypeMemberContext> typeMember() {
			return getRuleContexts(TypeMemberContext.class);
		}
		public TypeMemberContext typeMember(int i) {
			return getRuleContext(TypeMemberContext.class,i);
		}
		public List<TerminalNode> SemiColon() { return getTokens(TypeScriptParser.SemiColon); }
		public TerminalNode SemiColon(int i) {
			return getToken(TypeScriptParser.SemiColon, i);
		}
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public TypeMemberListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeMemberList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeMemberList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeMemberList(this);
		}
	}

	public final TypeMemberListContext typeMemberList() throws RecognitionException {
		TypeMemberListContext _localctx = new TypeMemberListContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_typeMemberList);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(424);
			typeMember();
			setState(429);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(425);
					_la = _input.LA(1);
					if ( !(_la==SemiColon || _la==Comma) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(426);
					typeMember();
					}
					} 
				}
				setState(431);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
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
	public static class TypeMemberContext extends ParserRuleContext {
		public PropertySignaturContext propertySignatur() {
			return getRuleContext(PropertySignaturContext.class,0);
		}
		public CallSignatureContext callSignature() {
			return getRuleContext(CallSignatureContext.class,0);
		}
		public ConstructSignatureContext constructSignature() {
			return getRuleContext(ConstructSignatureContext.class,0);
		}
		public IndexSignatureContext indexSignature() {
			return getRuleContext(IndexSignatureContext.class,0);
		}
		public MethodSignatureContext methodSignature() {
			return getRuleContext(MethodSignatureContext.class,0);
		}
		public TerminalNode ARROW() { return getToken(TypeScriptParser.ARROW, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TypeMemberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeMember; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeMember(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeMember(this);
		}
	}

	public final TypeMemberContext typeMember() throws RecognitionException {
		TypeMemberContext _localctx = new TypeMemberContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_typeMember);
		int _la;
		try {
			setState(441);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(432);
				propertySignatur();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(433);
				callSignature();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(434);
				constructSignature();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(435);
				indexSignature();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(436);
				methodSignature();
				setState(439);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ARROW) {
					{
					setState(437);
					match(ARROW);
					setState(438);
					type_();
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
	public static class ArrayTypeContext extends ParserRuleContext {
		public PrimaryTypeContext primaryType() {
			return getRuleContext(PrimaryTypeContext.class,0);
		}
		public TerminalNode OpenBracket() { return getToken(TypeScriptParser.OpenBracket, 0); }
		public TerminalNode CloseBracket() { return getToken(TypeScriptParser.CloseBracket, 0); }
		public ArrayTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterArrayType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitArrayType(this);
		}
	}

	public final ArrayTypeContext arrayType() throws RecognitionException {
		ArrayTypeContext _localctx = new ArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_arrayType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(443);
			primaryType(0);
			setState(444);
			if (!(notLineTerminator())) throw new FailedPredicateException(this, "notLineTerminator()");
			setState(445);
			match(OpenBracket);
			setState(446);
			match(CloseBracket);
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
	public static class TupleTypeContext extends ParserRuleContext {
		public TerminalNode OpenBracket() { return getToken(TypeScriptParser.OpenBracket, 0); }
		public TupleElementTypesContext tupleElementTypes() {
			return getRuleContext(TupleElementTypesContext.class,0);
		}
		public TerminalNode CloseBracket() { return getToken(TypeScriptParser.CloseBracket, 0); }
		public TupleTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tupleType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTupleType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTupleType(this);
		}
	}

	public final TupleTypeContext tupleType() throws RecognitionException {
		TupleTypeContext _localctx = new TupleTypeContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_tupleType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(448);
			match(OpenBracket);
			setState(449);
			tupleElementTypes();
			setState(450);
			match(CloseBracket);
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
	public static class TupleElementTypesContext extends ParserRuleContext {
		public List<Type_Context> type_() {
			return getRuleContexts(Type_Context.class);
		}
		public Type_Context type_(int i) {
			return getRuleContext(Type_Context.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public TupleElementTypesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tupleElementTypes; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTupleElementTypes(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTupleElementTypes(this);
		}
	}

	public final TupleElementTypesContext tupleElementTypes() throws RecognitionException {
		TupleElementTypesContext _localctx = new TupleElementTypesContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_tupleElementTypes);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(452);
			type_();
			setState(457);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(453);
				match(Comma);
				setState(454);
				type_();
				}
				}
				setState(459);
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
	public static class FunctionTypeContext extends ParserRuleContext {
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public TerminalNode ARROW() { return getToken(TypeScriptParser.ARROW, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TypeParametersContext typeParameters() {
			return getRuleContext(TypeParametersContext.class,0);
		}
		public ParameterListContext parameterList() {
			return getRuleContext(ParameterListContext.class,0);
		}
		public FunctionTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterFunctionType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitFunctionType(this);
		}
	}

	public final FunctionTypeContext functionType() throws RecognitionException {
		FunctionTypeContext _localctx = new FunctionTypeContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_functionType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(461);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LessThan) {
				{
				setState(460);
				typeParameters();
				}
			}

			setState(463);
			match(OpenParen);
			setState(465);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -4503599627370430192L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 1787629984886554623L) != 0)) {
				{
				setState(464);
				parameterList();
				}
			}

			setState(467);
			match(CloseParen);
			setState(468);
			match(ARROW);
			setState(469);
			type_();
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
	public static class ConstructorTypeContext extends ParserRuleContext {
		public TerminalNode New() { return getToken(TypeScriptParser.New, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public TerminalNode ARROW() { return getToken(TypeScriptParser.ARROW, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TypeParametersContext typeParameters() {
			return getRuleContext(TypeParametersContext.class,0);
		}
		public ParameterListContext parameterList() {
			return getRuleContext(ParameterListContext.class,0);
		}
		public ConstructorTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constructorType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterConstructorType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitConstructorType(this);
		}
	}

	public final ConstructorTypeContext constructorType() throws RecognitionException {
		ConstructorTypeContext _localctx = new ConstructorTypeContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_constructorType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(471);
			match(New);
			setState(473);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LessThan) {
				{
				setState(472);
				typeParameters();
				}
			}

			setState(475);
			match(OpenParen);
			setState(477);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -4503599627370430192L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 1787629984886554623L) != 0)) {
				{
				setState(476);
				parameterList();
				}
			}

			setState(479);
			match(CloseParen);
			setState(480);
			match(ARROW);
			setState(481);
			type_();
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
	public static class TypeQueryContext extends ParserRuleContext {
		public TerminalNode Typeof() { return getToken(TypeScriptParser.Typeof, 0); }
		public TypeQueryExpressionContext typeQueryExpression() {
			return getRuleContext(TypeQueryExpressionContext.class,0);
		}
		public TypeQueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeQuery; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeQuery(this);
		}
	}

	public final TypeQueryContext typeQuery() throws RecognitionException {
		TypeQueryContext _localctx = new TypeQueryContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_typeQuery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(483);
			match(Typeof);
			setState(484);
			typeQueryExpression();
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
	public static class TypeQueryExpressionContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public List<IdentifierNameContext> identifierName() {
			return getRuleContexts(IdentifierNameContext.class);
		}
		public IdentifierNameContext identifierName(int i) {
			return getRuleContext(IdentifierNameContext.class,i);
		}
		public List<TerminalNode> Dot() { return getTokens(TypeScriptParser.Dot); }
		public TerminalNode Dot(int i) {
			return getToken(TypeScriptParser.Dot, i);
		}
		public TypeQueryExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeQueryExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeQueryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeQueryExpression(this);
		}
	}

	public final TypeQueryExpressionContext typeQueryExpression() throws RecognitionException {
		TypeQueryExpressionContext _localctx = new TypeQueryExpressionContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_typeQueryExpression);
		try {
			int _alt;
			setState(496);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(486);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(490); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(487);
						identifierName();
						setState(488);
						match(Dot);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(492); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(494);
				identifierName();
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
	public static class PropertySignaturContext extends ParserRuleContext {
		public PropertyNameContext propertyName() {
			return getRuleContext(PropertyNameContext.class,0);
		}
		public TerminalNode ReadOnly() { return getToken(TypeScriptParser.ReadOnly, 0); }
		public TerminalNode QuestionMark() { return getToken(TypeScriptParser.QuestionMark, 0); }
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public TerminalNode ARROW() { return getToken(TypeScriptParser.ARROW, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public PropertySignaturContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_propertySignatur; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterPropertySignatur(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitPropertySignatur(this);
		}
	}

	public final PropertySignaturContext propertySignatur() throws RecognitionException {
		PropertySignaturContext _localctx = new PropertySignaturContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_propertySignatur);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(499);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				{
				setState(498);
				match(ReadOnly);
				}
				break;
			}
			setState(501);
			propertyName();
			setState(503);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==QuestionMark) {
				{
				setState(502);
				match(QuestionMark);
				}
			}

			setState(506);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Colon) {
				{
				setState(505);
				typeAnnotation();
				}
			}

			setState(510);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ARROW) {
				{
				setState(508);
				match(ARROW);
				setState(509);
				type_();
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
	public static class TypeAnnotationContext extends ParserRuleContext {
		public TerminalNode Colon() { return getToken(TypeScriptParser.Colon, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TypeAnnotationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeAnnotation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeAnnotation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeAnnotation(this);
		}
	}

	public final TypeAnnotationContext typeAnnotation() throws RecognitionException {
		TypeAnnotationContext _localctx = new TypeAnnotationContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_typeAnnotation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(512);
			match(Colon);
			setState(513);
			type_();
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
	public static class CallSignatureContext extends ParserRuleContext {
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public TypeParametersContext typeParameters() {
			return getRuleContext(TypeParametersContext.class,0);
		}
		public ParameterListContext parameterList() {
			return getRuleContext(ParameterListContext.class,0);
		}
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public CallSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_callSignature; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterCallSignature(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitCallSignature(this);
		}
	}

	public final CallSignatureContext callSignature() throws RecognitionException {
		CallSignatureContext _localctx = new CallSignatureContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_callSignature);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(516);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LessThan) {
				{
				setState(515);
				typeParameters();
				}
			}

			setState(518);
			match(OpenParen);
			setState(520);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -4503599627370430192L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 1787629984886554623L) != 0)) {
				{
				setState(519);
				parameterList();
				}
			}

			setState(522);
			match(CloseParen);
			setState(524);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				{
				setState(523);
				typeAnnotation();
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
	public static class ParameterListContext extends ParserRuleContext {
		public RestParameterContext restParameter() {
			return getRuleContext(RestParameterContext.class,0);
		}
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public ParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitParameterList(this);
		}
	}

	public final ParameterListContext parameterList() throws RecognitionException {
		ParameterListContext _localctx = new ParameterListContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_parameterList);
		int _la;
		try {
			int _alt;
			setState(539);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Ellipsis:
				enterOuterAlt(_localctx, 1);
				{
				setState(526);
				restParameter();
				}
				break;
			case OpenBracket:
			case OpenBrace:
			case NullLiteral:
			case BooleanLiteral:
			case Break:
			case Do:
			case Instanceof:
			case Typeof:
			case Case:
			case Else:
			case New:
			case Var:
			case Catch:
			case Finally:
			case Return:
			case Void:
			case Continue:
			case For:
			case Switch:
			case While:
			case Debugger:
			case Function_:
			case This:
			case With:
			case Default:
			case If:
			case Throw:
			case Delete:
			case In:
			case Try:
			case From:
			case ReadOnly:
			case Async:
			case Class:
			case Enum:
			case Extends:
			case Super:
			case Const:
			case Export:
			case Import:
			case Implements:
			case Let:
			case Private:
			case Public:
			case Interface:
			case Package:
			case Protected:
			case Static:
			case Yield:
			case Number:
			case Boolean:
			case String:
			case TypeAlias:
			case Get:
			case Set:
			case Require:
			case Module:
			case At:
			case Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(527);
				parameter();
				setState(532);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(528);
						match(Comma);
						setState(529);
						parameter();
						}
						} 
					}
					setState(534);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
				}
				setState(537);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(535);
					match(Comma);
					setState(536);
					restParameter();
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

	@SuppressWarnings("CheckReturnValue")
	public static class RequiredParameterListContext extends ParserRuleContext {
		public List<RequiredParameterContext> requiredParameter() {
			return getRuleContexts(RequiredParameterContext.class);
		}
		public RequiredParameterContext requiredParameter(int i) {
			return getRuleContext(RequiredParameterContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public RequiredParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_requiredParameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterRequiredParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitRequiredParameterList(this);
		}
	}

	public final RequiredParameterListContext requiredParameterList() throws RecognitionException {
		RequiredParameterListContext _localctx = new RequiredParameterListContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_requiredParameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(541);
			requiredParameter();
			setState(546);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(542);
				match(Comma);
				setState(543);
				requiredParameter();
				}
				}
				setState(548);
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
	public static class ParameterContext extends ParserRuleContext {
		public RequiredParameterContext requiredParameter() {
			return getRuleContext(RequiredParameterContext.class,0);
		}
		public OptionalParameterContext optionalParameter() {
			return getRuleContext(OptionalParameterContext.class,0);
		}
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitParameter(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_parameter);
		try {
			setState(551);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(549);
				requiredParameter();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(550);
				optionalParameter();
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
	public static class OptionalParameterContext extends ParserRuleContext {
		public IdentifierOrPatternContext identifierOrPattern() {
			return getRuleContext(IdentifierOrPatternContext.class,0);
		}
		public DecoratorListContext decoratorList() {
			return getRuleContext(DecoratorListContext.class,0);
		}
		public TerminalNode QuestionMark() { return getToken(TypeScriptParser.QuestionMark, 0); }
		public InitializerContext initializer() {
			return getRuleContext(InitializerContext.class,0);
		}
		public AccessibilityModifierContext accessibilityModifier() {
			return getRuleContext(AccessibilityModifierContext.class,0);
		}
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public OptionalParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optionalParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterOptionalParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitOptionalParameter(this);
		}
	}

	public final OptionalParameterContext optionalParameter() throws RecognitionException {
		OptionalParameterContext _localctx = new OptionalParameterContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_optionalParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(554);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==At) {
				{
				setState(553);
				decoratorList();
				}
			}

			{
			setState(557);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				{
				setState(556);
				accessibilityModifier();
				}
				break;
			}
			setState(559);
			identifierOrPattern();
			setState(568);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case QuestionMark:
				{
				setState(560);
				match(QuestionMark);
				setState(562);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Colon) {
					{
					setState(561);
					typeAnnotation();
					}
				}

				}
				break;
			case Assign:
			case Colon:
				{
				setState(565);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Colon) {
					{
					setState(564);
					typeAnnotation();
					}
				}

				setState(567);
				initializer();
				}
				break;
			default:
				throw new NoViableAltException(this);
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
	public static class RestParameterContext extends ParserRuleContext {
		public TerminalNode Ellipsis() { return getToken(TypeScriptParser.Ellipsis, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public RestParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_restParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterRestParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitRestParameter(this);
		}
	}

	public final RestParameterContext restParameter() throws RecognitionException {
		RestParameterContext _localctx = new RestParameterContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_restParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(570);
			match(Ellipsis);
			setState(571);
			singleExpression(0);
			setState(573);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Colon) {
				{
				setState(572);
				typeAnnotation();
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
	public static class RequiredParameterContext extends ParserRuleContext {
		public IdentifierOrPatternContext identifierOrPattern() {
			return getRuleContext(IdentifierOrPatternContext.class,0);
		}
		public DecoratorListContext decoratorList() {
			return getRuleContext(DecoratorListContext.class,0);
		}
		public AccessibilityModifierContext accessibilityModifier() {
			return getRuleContext(AccessibilityModifierContext.class,0);
		}
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public RequiredParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_requiredParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterRequiredParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitRequiredParameter(this);
		}
	}

	public final RequiredParameterContext requiredParameter() throws RecognitionException {
		RequiredParameterContext _localctx = new RequiredParameterContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_requiredParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(576);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==At) {
				{
				setState(575);
				decoratorList();
				}
			}

			setState(579);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				{
				setState(578);
				accessibilityModifier();
				}
				break;
			}
			setState(581);
			identifierOrPattern();
			setState(583);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Colon) {
				{
				setState(582);
				typeAnnotation();
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
	public static class AccessibilityModifierContext extends ParserRuleContext {
		public TerminalNode Public() { return getToken(TypeScriptParser.Public, 0); }
		public TerminalNode Private() { return getToken(TypeScriptParser.Private, 0); }
		public TerminalNode Protected() { return getToken(TypeScriptParser.Protected, 0); }
		public AccessibilityModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_accessibilityModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterAccessibilityModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitAccessibilityModifier(this);
		}
	}

	public final AccessibilityModifierContext accessibilityModifier() throws RecognitionException {
		AccessibilityModifierContext _localctx = new AccessibilityModifierContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_accessibilityModifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(585);
			_la = _input.LA(1);
			if ( !(((((_la - 101)) & ~0x3f) == 0 && ((1L << (_la - 101)) & 19L) != 0)) ) {
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
	public static class IdentifierOrPatternContext extends ParserRuleContext {
		public IdentifierNameContext identifierName() {
			return getRuleContext(IdentifierNameContext.class,0);
		}
		public BindingPatternContext bindingPattern() {
			return getRuleContext(BindingPatternContext.class,0);
		}
		public IdentifierOrPatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifierOrPattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterIdentifierOrPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitIdentifierOrPattern(this);
		}
	}

	public final IdentifierOrPatternContext identifierOrPattern() throws RecognitionException {
		IdentifierOrPatternContext _localctx = new IdentifierOrPatternContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_identifierOrPattern);
		try {
			setState(589);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NullLiteral:
			case BooleanLiteral:
			case Break:
			case Do:
			case Instanceof:
			case Typeof:
			case Case:
			case Else:
			case New:
			case Var:
			case Catch:
			case Finally:
			case Return:
			case Void:
			case Continue:
			case For:
			case Switch:
			case While:
			case Debugger:
			case Function_:
			case This:
			case With:
			case Default:
			case If:
			case Throw:
			case Delete:
			case In:
			case Try:
			case From:
			case ReadOnly:
			case Async:
			case Class:
			case Enum:
			case Extends:
			case Super:
			case Const:
			case Export:
			case Import:
			case Implements:
			case Let:
			case Private:
			case Public:
			case Interface:
			case Package:
			case Protected:
			case Static:
			case Yield:
			case Number:
			case Boolean:
			case String:
			case TypeAlias:
			case Get:
			case Set:
			case Require:
			case Module:
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(587);
				identifierName();
				}
				break;
			case OpenBracket:
			case OpenBrace:
				enterOuterAlt(_localctx, 2);
				{
				setState(588);
				bindingPattern();
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
	public static class ConstructSignatureContext extends ParserRuleContext {
		public TerminalNode New() { return getToken(TypeScriptParser.New, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public TypeParametersContext typeParameters() {
			return getRuleContext(TypeParametersContext.class,0);
		}
		public ParameterListContext parameterList() {
			return getRuleContext(ParameterListContext.class,0);
		}
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public ConstructSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constructSignature; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterConstructSignature(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitConstructSignature(this);
		}
	}

	public final ConstructSignatureContext constructSignature() throws RecognitionException {
		ConstructSignatureContext _localctx = new ConstructSignatureContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_constructSignature);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(591);
			match(New);
			setState(593);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LessThan) {
				{
				setState(592);
				typeParameters();
				}
			}

			setState(595);
			match(OpenParen);
			setState(597);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -4503599627370430192L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 1787629984886554623L) != 0)) {
				{
				setState(596);
				parameterList();
				}
			}

			setState(599);
			match(CloseParen);
			setState(601);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Colon) {
				{
				setState(600);
				typeAnnotation();
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
	public static class IndexSignatureContext extends ParserRuleContext {
		public TerminalNode OpenBracket() { return getToken(TypeScriptParser.OpenBracket, 0); }
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public TerminalNode Colon() { return getToken(TypeScriptParser.Colon, 0); }
		public TerminalNode CloseBracket() { return getToken(TypeScriptParser.CloseBracket, 0); }
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public TerminalNode Number() { return getToken(TypeScriptParser.Number, 0); }
		public TerminalNode String() { return getToken(TypeScriptParser.String, 0); }
		public IndexSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_indexSignature; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterIndexSignature(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitIndexSignature(this);
		}
	}

	public final IndexSignatureContext indexSignature() throws RecognitionException {
		IndexSignatureContext _localctx = new IndexSignatureContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_indexSignature);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(603);
			match(OpenBracket);
			setState(604);
			match(Identifier);
			setState(605);
			match(Colon);
			setState(606);
			_la = _input.LA(1);
			if ( !(_la==Number || _la==String) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(607);
			match(CloseBracket);
			setState(608);
			typeAnnotation();
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
	public static class MethodSignatureContext extends ParserRuleContext {
		public PropertyNameContext propertyName() {
			return getRuleContext(PropertyNameContext.class,0);
		}
		public CallSignatureContext callSignature() {
			return getRuleContext(CallSignatureContext.class,0);
		}
		public TerminalNode QuestionMark() { return getToken(TypeScriptParser.QuestionMark, 0); }
		public MethodSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodSignature; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterMethodSignature(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitMethodSignature(this);
		}
	}

	public final MethodSignatureContext methodSignature() throws RecognitionException {
		MethodSignatureContext _localctx = new MethodSignatureContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_methodSignature);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(610);
			propertyName();
			setState(612);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==QuestionMark) {
				{
				setState(611);
				match(QuestionMark);
				}
			}

			setState(614);
			callSignature();
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
	public static class TypeAliasDeclarationContext extends ParserRuleContext {
		public TerminalNode TypeAlias() { return getToken(TypeScriptParser.TypeAlias, 0); }
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public TerminalNode Assign() { return getToken(TypeScriptParser.Assign, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TerminalNode SemiColon() { return getToken(TypeScriptParser.SemiColon, 0); }
		public TypeParametersContext typeParameters() {
			return getRuleContext(TypeParametersContext.class,0);
		}
		public TypeAliasDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeAliasDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeAliasDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeAliasDeclaration(this);
		}
	}

	public final TypeAliasDeclarationContext typeAliasDeclaration() throws RecognitionException {
		TypeAliasDeclarationContext _localctx = new TypeAliasDeclarationContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_typeAliasDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(616);
			match(TypeAlias);
			setState(617);
			match(Identifier);
			setState(619);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LessThan) {
				{
				setState(618);
				typeParameters();
				}
			}

			setState(621);
			match(Assign);
			setState(622);
			type_();
			setState(623);
			match(SemiColon);
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
	public static class ConstructorDeclarationContext extends ParserRuleContext {
		public TerminalNode Constructor() { return getToken(TypeScriptParser.Constructor, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public AccessibilityModifierContext accessibilityModifier() {
			return getRuleContext(AccessibilityModifierContext.class,0);
		}
		public FormalParameterListContext formalParameterList() {
			return getRuleContext(FormalParameterListContext.class,0);
		}
		public TerminalNode SemiColon() { return getToken(TypeScriptParser.SemiColon, 0); }
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public ConstructorDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constructorDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterConstructorDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitConstructorDeclaration(this);
		}
	}

	public final ConstructorDeclarationContext constructorDeclaration() throws RecognitionException {
		ConstructorDeclarationContext _localctx = new ConstructorDeclarationContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_constructorDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(626);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 101)) & ~0x3f) == 0 && ((1L << (_la - 101)) & 19L) != 0)) {
				{
				setState(625);
				accessibilityModifier();
				}
			}

			setState(628);
			match(Constructor);
			setState(629);
			match(OpenParen);
			setState(631);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 65808L) != 0) || ((((_la - 101)) & ~0x3f) == 0 && ((1L << (_la - 101)) & 12718099L) != 0)) {
				{
				setState(630);
				formalParameterList();
				}
			}

			setState(633);
			match(CloseParen);
			setState(639);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				{
				{
				setState(634);
				match(OpenBrace);
				setState(635);
				functionBody();
				setState(636);
				match(CloseBrace);
				}
				}
				break;
			case 2:
				{
				setState(638);
				match(SemiColon);
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
	public static class InterfaceDeclarationContext extends ParserRuleContext {
		public TerminalNode Interface() { return getToken(TypeScriptParser.Interface, 0); }
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public ObjectTypeContext objectType() {
			return getRuleContext(ObjectTypeContext.class,0);
		}
		public TerminalNode Export() { return getToken(TypeScriptParser.Export, 0); }
		public TerminalNode Declare() { return getToken(TypeScriptParser.Declare, 0); }
		public TypeParametersContext typeParameters() {
			return getRuleContext(TypeParametersContext.class,0);
		}
		public InterfaceExtendsClauseContext interfaceExtendsClause() {
			return getRuleContext(InterfaceExtendsClauseContext.class,0);
		}
		public TerminalNode SemiColon() { return getToken(TypeScriptParser.SemiColon, 0); }
		public InterfaceDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterInterfaceDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitInterfaceDeclaration(this);
		}
	}

	public final InterfaceDeclarationContext interfaceDeclaration() throws RecognitionException {
		InterfaceDeclarationContext _localctx = new InterfaceDeclarationContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_interfaceDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(642);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Export) {
				{
				setState(641);
				match(Export);
				}
			}

			setState(645);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Declare) {
				{
				setState(644);
				match(Declare);
				}
			}

			setState(647);
			match(Interface);
			setState(648);
			match(Identifier);
			setState(650);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LessThan) {
				{
				setState(649);
				typeParameters();
				}
			}

			setState(653);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Extends) {
				{
				setState(652);
				interfaceExtendsClause();
				}
			}

			setState(655);
			objectType();
			setState(657);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
			case 1:
				{
				setState(656);
				match(SemiColon);
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
	public static class InterfaceExtendsClauseContext extends ParserRuleContext {
		public TerminalNode Extends() { return getToken(TypeScriptParser.Extends, 0); }
		public ClassOrInterfaceTypeListContext classOrInterfaceTypeList() {
			return getRuleContext(ClassOrInterfaceTypeListContext.class,0);
		}
		public InterfaceExtendsClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceExtendsClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterInterfaceExtendsClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitInterfaceExtendsClause(this);
		}
	}

	public final InterfaceExtendsClauseContext interfaceExtendsClause() throws RecognitionException {
		InterfaceExtendsClauseContext _localctx = new InterfaceExtendsClauseContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_interfaceExtendsClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(659);
			match(Extends);
			setState(660);
			classOrInterfaceTypeList();
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
	public static class ClassOrInterfaceTypeListContext extends ParserRuleContext {
		public List<TypeReferenceContext> typeReference() {
			return getRuleContexts(TypeReferenceContext.class);
		}
		public TypeReferenceContext typeReference(int i) {
			return getRuleContext(TypeReferenceContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public ClassOrInterfaceTypeListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classOrInterfaceTypeList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterClassOrInterfaceTypeList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitClassOrInterfaceTypeList(this);
		}
	}

	public final ClassOrInterfaceTypeListContext classOrInterfaceTypeList() throws RecognitionException {
		ClassOrInterfaceTypeListContext _localctx = new ClassOrInterfaceTypeListContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_classOrInterfaceTypeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(662);
			typeReference();
			setState(667);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(663);
				match(Comma);
				setState(664);
				typeReference();
				}
				}
				setState(669);
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
	public static class EnumDeclarationContext extends ParserRuleContext {
		public TerminalNode Enum() { return getToken(TypeScriptParser.Enum, 0); }
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public TerminalNode Const() { return getToken(TypeScriptParser.Const, 0); }
		public EnumBodyContext enumBody() {
			return getRuleContext(EnumBodyContext.class,0);
		}
		public EnumDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterEnumDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitEnumDeclaration(this);
		}
	}

	public final EnumDeclarationContext enumDeclaration() throws RecognitionException {
		EnumDeclarationContext _localctx = new EnumDeclarationContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_enumDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(671);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Const) {
				{
				setState(670);
				match(Const);
				}
			}

			setState(673);
			match(Enum);
			setState(674);
			match(Identifier);
			setState(675);
			match(OpenBrace);
			setState(677);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -36028797018963968L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 3517012241796825087L) != 0)) {
				{
				setState(676);
				enumBody();
				}
			}

			setState(679);
			match(CloseBrace);
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
	public static class EnumBodyContext extends ParserRuleContext {
		public EnumMemberListContext enumMemberList() {
			return getRuleContext(EnumMemberListContext.class,0);
		}
		public TerminalNode Comma() { return getToken(TypeScriptParser.Comma, 0); }
		public EnumBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterEnumBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitEnumBody(this);
		}
	}

	public final EnumBodyContext enumBody() throws RecognitionException {
		EnumBodyContext _localctx = new EnumBodyContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_enumBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(681);
			enumMemberList();
			setState(683);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Comma) {
				{
				setState(682);
				match(Comma);
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
	public static class EnumMemberListContext extends ParserRuleContext {
		public List<EnumMemberContext> enumMember() {
			return getRuleContexts(EnumMemberContext.class);
		}
		public EnumMemberContext enumMember(int i) {
			return getRuleContext(EnumMemberContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public EnumMemberListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumMemberList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterEnumMemberList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitEnumMemberList(this);
		}
	}

	public final EnumMemberListContext enumMemberList() throws RecognitionException {
		EnumMemberListContext _localctx = new EnumMemberListContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_enumMemberList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(685);
			enumMember();
			setState(690);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,67,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(686);
					match(Comma);
					setState(687);
					enumMember();
					}
					} 
				}
				setState(692);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,67,_ctx);
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
	public static class EnumMemberContext extends ParserRuleContext {
		public PropertyNameContext propertyName() {
			return getRuleContext(PropertyNameContext.class,0);
		}
		public TerminalNode Assign() { return getToken(TypeScriptParser.Assign, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public EnumMemberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumMember; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterEnumMember(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitEnumMember(this);
		}
	}

	public final EnumMemberContext enumMember() throws RecognitionException {
		EnumMemberContext _localctx = new EnumMemberContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_enumMember);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(693);
			propertyName();
			setState(696);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Assign) {
				{
				setState(694);
				match(Assign);
				setState(695);
				singleExpression(0);
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
	public static class NamespaceDeclarationContext extends ParserRuleContext {
		public TerminalNode Namespace() { return getToken(TypeScriptParser.Namespace, 0); }
		public NamespaceNameContext namespaceName() {
			return getRuleContext(NamespaceNameContext.class,0);
		}
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public NamespaceDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_namespaceDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterNamespaceDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitNamespaceDeclaration(this);
		}
	}

	public final NamespaceDeclarationContext namespaceDeclaration() throws RecognitionException {
		NamespaceDeclarationContext _localctx = new NamespaceDeclarationContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_namespaceDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(698);
			match(Namespace);
			setState(699);
			namespaceName();
			setState(700);
			match(OpenBrace);
			setState(702);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
			case 1:
				{
				setState(701);
				statementList();
				}
				break;
			}
			setState(704);
			match(CloseBrace);
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
	public static class NamespaceNameContext extends ParserRuleContext {
		public List<TerminalNode> Identifier() { return getTokens(TypeScriptParser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(TypeScriptParser.Identifier, i);
		}
		public List<TerminalNode> Dot() { return getTokens(TypeScriptParser.Dot); }
		public TerminalNode Dot(int i) {
			return getToken(TypeScriptParser.Dot, i);
		}
		public NamespaceNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_namespaceName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterNamespaceName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitNamespaceName(this);
		}
	}

	public final NamespaceNameContext namespaceName() throws RecognitionException {
		NamespaceNameContext _localctx = new NamespaceNameContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_namespaceName);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(706);
			match(Identifier);
			setState(715);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,71,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(708); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(707);
						match(Dot);
						}
						}
						setState(710); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==Dot );
					setState(712);
					match(Identifier);
					}
					} 
				}
				setState(717);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,71,_ctx);
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
	public static class ImportAliasDeclarationContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public TerminalNode Assign() { return getToken(TypeScriptParser.Assign, 0); }
		public NamespaceNameContext namespaceName() {
			return getRuleContext(NamespaceNameContext.class,0);
		}
		public TerminalNode SemiColon() { return getToken(TypeScriptParser.SemiColon, 0); }
		public ImportAliasDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importAliasDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterImportAliasDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitImportAliasDeclaration(this);
		}
	}

	public final ImportAliasDeclarationContext importAliasDeclaration() throws RecognitionException {
		ImportAliasDeclarationContext _localctx = new ImportAliasDeclarationContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_importAliasDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(718);
			match(Identifier);
			setState(719);
			match(Assign);
			setState(720);
			namespaceName();
			setState(721);
			match(SemiColon);
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
	public static class DecoratorListContext extends ParserRuleContext {
		public List<DecoratorContext> decorator() {
			return getRuleContexts(DecoratorContext.class);
		}
		public DecoratorContext decorator(int i) {
			return getRuleContext(DecoratorContext.class,i);
		}
		public DecoratorListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decoratorList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterDecoratorList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitDecoratorList(this);
		}
	}

	public final DecoratorListContext decoratorList() throws RecognitionException {
		DecoratorListContext _localctx = new DecoratorListContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_decoratorList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(724); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(723);
				decorator();
				}
				}
				setState(726); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==At );
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
		public TerminalNode At() { return getToken(TypeScriptParser.At, 0); }
		public DecoratorMemberExpressionContext decoratorMemberExpression() {
			return getRuleContext(DecoratorMemberExpressionContext.class,0);
		}
		public DecoratorCallExpressionContext decoratorCallExpression() {
			return getRuleContext(DecoratorCallExpressionContext.class,0);
		}
		public DecoratorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decorator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterDecorator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitDecorator(this);
		}
	}

	public final DecoratorContext decorator() throws RecognitionException {
		DecoratorContext _localctx = new DecoratorContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_decorator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(728);
			match(At);
			setState(731);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
			case 1:
				{
				setState(729);
				decoratorMemberExpression(0);
				}
				break;
			case 2:
				{
				setState(730);
				decoratorCallExpression();
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
	public static class DecoratorMemberExpressionContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public DecoratorMemberExpressionContext decoratorMemberExpression() {
			return getRuleContext(DecoratorMemberExpressionContext.class,0);
		}
		public TerminalNode Dot() { return getToken(TypeScriptParser.Dot, 0); }
		public IdentifierNameContext identifierName() {
			return getRuleContext(IdentifierNameContext.class,0);
		}
		public DecoratorMemberExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decoratorMemberExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterDecoratorMemberExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitDecoratorMemberExpression(this);
		}
	}

	public final DecoratorMemberExpressionContext decoratorMemberExpression() throws RecognitionException {
		return decoratorMemberExpression(0);
	}

	private DecoratorMemberExpressionContext decoratorMemberExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		DecoratorMemberExpressionContext _localctx = new DecoratorMemberExpressionContext(_ctx, _parentState);
		DecoratorMemberExpressionContext _prevctx = _localctx;
		int _startState = 114;
		enterRecursionRule(_localctx, 114, RULE_decoratorMemberExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(739);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				{
				setState(734);
				match(Identifier);
				}
				break;
			case OpenParen:
				{
				setState(735);
				match(OpenParen);
				setState(736);
				singleExpression(0);
				setState(737);
				match(CloseParen);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(746);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,75,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new DecoratorMemberExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_decoratorMemberExpression);
					setState(741);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(742);
					match(Dot);
					setState(743);
					identifierName();
					}
					} 
				}
				setState(748);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,75,_ctx);
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
	public static class DecoratorCallExpressionContext extends ParserRuleContext {
		public DecoratorMemberExpressionContext decoratorMemberExpression() {
			return getRuleContext(DecoratorMemberExpressionContext.class,0);
		}
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public DecoratorCallExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decoratorCallExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterDecoratorCallExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitDecoratorCallExpression(this);
		}
	}

	public final DecoratorCallExpressionContext decoratorCallExpression() throws RecognitionException {
		DecoratorCallExpressionContext _localctx = new DecoratorCallExpressionContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_decoratorCallExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(749);
			decoratorMemberExpression(0);
			setState(750);
			arguments();
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
	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(TypeScriptParser.EOF, 0); }
		public SourceElementsContext sourceElements() {
			return getRuleContext(SourceElementsContext.class,0);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitProgram(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_program);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(753);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
			case 1:
				{
				setState(752);
				sourceElements();
				}
				break;
			}
			setState(755);
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
	public static class SourceElementContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public TerminalNode Export() { return getToken(TypeScriptParser.Export, 0); }
		public SourceElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sourceElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterSourceElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitSourceElement(this);
		}
	}

	public final SourceElementContext sourceElement() throws RecognitionException {
		SourceElementContext _localctx = new SourceElementContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_sourceElement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(758);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
			case 1:
				{
				setState(757);
				match(Export);
				}
				break;
			}
			setState(760);
			statement();
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
	public static class StatementContext extends ParserRuleContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public ImportStatementContext importStatement() {
			return getRuleContext(ImportStatementContext.class,0);
		}
		public ExportStatementContext exportStatement() {
			return getRuleContext(ExportStatementContext.class,0);
		}
		public EmptyStatement_Context emptyStatement_() {
			return getRuleContext(EmptyStatement_Context.class,0);
		}
		public AbstractDeclarationContext abstractDeclaration() {
			return getRuleContext(AbstractDeclarationContext.class,0);
		}
		public ClassDeclarationContext classDeclaration() {
			return getRuleContext(ClassDeclarationContext.class,0);
		}
		public InterfaceDeclarationContext interfaceDeclaration() {
			return getRuleContext(InterfaceDeclarationContext.class,0);
		}
		public NamespaceDeclarationContext namespaceDeclaration() {
			return getRuleContext(NamespaceDeclarationContext.class,0);
		}
		public IfStatementContext ifStatement() {
			return getRuleContext(IfStatementContext.class,0);
		}
		public IterationStatementContext iterationStatement() {
			return getRuleContext(IterationStatementContext.class,0);
		}
		public ContinueStatementContext continueStatement() {
			return getRuleContext(ContinueStatementContext.class,0);
		}
		public BreakStatementContext breakStatement() {
			return getRuleContext(BreakStatementContext.class,0);
		}
		public ReturnStatementContext returnStatement() {
			return getRuleContext(ReturnStatementContext.class,0);
		}
		public YieldStatementContext yieldStatement() {
			return getRuleContext(YieldStatementContext.class,0);
		}
		public WithStatementContext withStatement() {
			return getRuleContext(WithStatementContext.class,0);
		}
		public LabelledStatementContext labelledStatement() {
			return getRuleContext(LabelledStatementContext.class,0);
		}
		public SwitchStatementContext switchStatement() {
			return getRuleContext(SwitchStatementContext.class,0);
		}
		public ThrowStatementContext throwStatement() {
			return getRuleContext(ThrowStatementContext.class,0);
		}
		public TryStatementContext tryStatement() {
			return getRuleContext(TryStatementContext.class,0);
		}
		public DebuggerStatementContext debuggerStatement() {
			return getRuleContext(DebuggerStatementContext.class,0);
		}
		public FunctionDeclarationContext functionDeclaration() {
			return getRuleContext(FunctionDeclarationContext.class,0);
		}
		public ArrowFunctionDeclarationContext arrowFunctionDeclaration() {
			return getRuleContext(ArrowFunctionDeclarationContext.class,0);
		}
		public GeneratorFunctionDeclarationContext generatorFunctionDeclaration() {
			return getRuleContext(GeneratorFunctionDeclarationContext.class,0);
		}
		public VariableStatementContext variableStatement() {
			return getRuleContext(VariableStatementContext.class,0);
		}
		public TypeAliasDeclarationContext typeAliasDeclaration() {
			return getRuleContext(TypeAliasDeclarationContext.class,0);
		}
		public EnumDeclarationContext enumDeclaration() {
			return getRuleContext(EnumDeclarationContext.class,0);
		}
		public ExpressionStatementContext expressionStatement() {
			return getRuleContext(ExpressionStatementContext.class,0);
		}
		public TerminalNode Export() { return getToken(TypeScriptParser.Export, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_statement);
		try {
			setState(791);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,78,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(762);
				block();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(763);
				importStatement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(764);
				exportStatement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(765);
				emptyStatement_();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(766);
				abstractDeclaration();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(767);
				classDeclaration();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(768);
				interfaceDeclaration();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(769);
				namespaceDeclaration();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(770);
				ifStatement();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(771);
				iterationStatement();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(772);
				continueStatement();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(773);
				breakStatement();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(774);
				returnStatement();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(775);
				yieldStatement();
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(776);
				withStatement();
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(777);
				labelledStatement();
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(778);
				switchStatement();
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(779);
				throwStatement();
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(780);
				tryStatement();
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(781);
				debuggerStatement();
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(782);
				functionDeclaration();
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(783);
				arrowFunctionDeclaration();
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(784);
				generatorFunctionDeclaration();
				}
				break;
			case 24:
				enterOuterAlt(_localctx, 24);
				{
				setState(785);
				variableStatement();
				}
				break;
			case 25:
				enterOuterAlt(_localctx, 25);
				{
				setState(786);
				typeAliasDeclaration();
				}
				break;
			case 26:
				enterOuterAlt(_localctx, 26);
				{
				setState(787);
				enumDeclaration();
				}
				break;
			case 27:
				enterOuterAlt(_localctx, 27);
				{
				setState(788);
				expressionStatement();
				}
				break;
			case 28:
				enterOuterAlt(_localctx, 28);
				{
				setState(789);
				match(Export);
				setState(790);
				statement();
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
	public static class BlockContext extends ParserRuleContext {
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitBlock(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(793);
			match(OpenBrace);
			setState(795);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
			case 1:
				{
				setState(794);
				statementList();
				}
				break;
			}
			setState(797);
			match(CloseBrace);
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
	public static class StatementListContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public StatementListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statementList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterStatementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitStatementList(this);
		}
	}

	public final StatementListContext statementList() throws RecognitionException {
		StatementListContext _localctx = new StatementListContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_statementList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(800); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(799);
					statement();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(802); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,80,_ctx);
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
	public static class AbstractDeclarationContext extends ParserRuleContext {
		public TerminalNode Abstract() { return getToken(TypeScriptParser.Abstract, 0); }
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public CallSignatureContext callSignature() {
			return getRuleContext(CallSignatureContext.class,0);
		}
		public VariableStatementContext variableStatement() {
			return getRuleContext(VariableStatementContext.class,0);
		}
		public AbstractDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_abstractDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterAbstractDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitAbstractDeclaration(this);
		}
	}

	public final AbstractDeclarationContext abstractDeclaration() throws RecognitionException {
		AbstractDeclarationContext _localctx = new AbstractDeclarationContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_abstractDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(804);
			match(Abstract);
			setState(808);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,81,_ctx) ) {
			case 1:
				{
				setState(805);
				match(Identifier);
				setState(806);
				callSignature();
				}
				break;
			case 2:
				{
				setState(807);
				variableStatement();
				}
				break;
			}
			setState(810);
			eos();
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
	public static class ImportStatementContext extends ParserRuleContext {
		public TerminalNode Import() { return getToken(TypeScriptParser.Import, 0); }
		public FromBlockContext fromBlock() {
			return getRuleContext(FromBlockContext.class,0);
		}
		public ImportAliasDeclarationContext importAliasDeclaration() {
			return getRuleContext(ImportAliasDeclarationContext.class,0);
		}
		public ImportStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterImportStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitImportStatement(this);
		}
	}

	public final ImportStatementContext importStatement() throws RecognitionException {
		ImportStatementContext _localctx = new ImportStatementContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_importStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(812);
			match(Import);
			setState(815);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
			case 1:
				{
				setState(813);
				fromBlock();
				}
				break;
			case 2:
				{
				setState(814);
				importAliasDeclaration();
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
	public static class FromBlockContext extends ParserRuleContext {
		public TerminalNode From() { return getToken(TypeScriptParser.From, 0); }
		public TerminalNode StringLiteral() { return getToken(TypeScriptParser.StringLiteral, 0); }
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public TerminalNode Multiply() { return getToken(TypeScriptParser.Multiply, 0); }
		public MultipleImportStatementContext multipleImportStatement() {
			return getRuleContext(MultipleImportStatementContext.class,0);
		}
		public TerminalNode As() { return getToken(TypeScriptParser.As, 0); }
		public IdentifierNameContext identifierName() {
			return getRuleContext(IdentifierNameContext.class,0);
		}
		public FromBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fromBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterFromBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitFromBlock(this);
		}
	}

	public final FromBlockContext fromBlock() throws RecognitionException {
		FromBlockContext _localctx = new FromBlockContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_fromBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(819);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Multiply:
				{
				setState(817);
				match(Multiply);
				}
				break;
			case OpenBrace:
			case NullLiteral:
			case BooleanLiteral:
			case Break:
			case Do:
			case Instanceof:
			case Typeof:
			case Case:
			case Else:
			case New:
			case Var:
			case Catch:
			case Finally:
			case Return:
			case Void:
			case Continue:
			case For:
			case Switch:
			case While:
			case Debugger:
			case Function_:
			case This:
			case With:
			case Default:
			case If:
			case Throw:
			case Delete:
			case In:
			case Try:
			case From:
			case ReadOnly:
			case Async:
			case Class:
			case Enum:
			case Extends:
			case Super:
			case Const:
			case Export:
			case Import:
			case Implements:
			case Let:
			case Private:
			case Public:
			case Interface:
			case Package:
			case Protected:
			case Static:
			case Yield:
			case Number:
			case Boolean:
			case String:
			case TypeAlias:
			case Get:
			case Set:
			case Require:
			case Module:
			case Identifier:
				{
				setState(818);
				multipleImportStatement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(823);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==As) {
				{
				setState(821);
				match(As);
				setState(822);
				identifierName();
				}
			}

			setState(825);
			match(From);
			setState(826);
			match(StringLiteral);
			setState(827);
			eos();
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
	public static class MultipleImportStatementContext extends ParserRuleContext {
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public List<IdentifierNameContext> identifierName() {
			return getRuleContexts(IdentifierNameContext.class);
		}
		public IdentifierNameContext identifierName(int i) {
			return getRuleContext(IdentifierNameContext.class,i);
		}
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public MultipleImportStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multipleImportStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterMultipleImportStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitMultipleImportStatement(this);
		}
	}

	public final MultipleImportStatementContext multipleImportStatement() throws RecognitionException {
		MultipleImportStatementContext _localctx = new MultipleImportStatementContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_multipleImportStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(832);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -4503599627370496000L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 1211169232583131135L) != 0)) {
				{
				setState(829);
				identifierName();
				setState(830);
				match(Comma);
				}
			}

			setState(834);
			match(OpenBrace);
			setState(835);
			identifierName();
			setState(840);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(836);
				match(Comma);
				setState(837);
				identifierName();
				}
				}
				setState(842);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(843);
			match(CloseBrace);
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
	public static class ExportStatementContext extends ParserRuleContext {
		public TerminalNode Export() { return getToken(TypeScriptParser.Export, 0); }
		public FromBlockContext fromBlock() {
			return getRuleContext(FromBlockContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public TerminalNode Default() { return getToken(TypeScriptParser.Default, 0); }
		public ExportStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exportStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterExportStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitExportStatement(this);
		}
	}

	public final ExportStatementContext exportStatement() throws RecognitionException {
		ExportStatementContext _localctx = new ExportStatementContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_exportStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(845);
			match(Export);
			setState(847);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
			case 1:
				{
				setState(846);
				match(Default);
				}
				break;
			}
			setState(851);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,88,_ctx) ) {
			case 1:
				{
				setState(849);
				fromBlock();
				}
				break;
			case 2:
				{
				setState(850);
				statement();
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
	public static class VariableStatementContext extends ParserRuleContext {
		public BindingPatternContext bindingPattern() {
			return getRuleContext(BindingPatternContext.class,0);
		}
		public InitializerContext initializer() {
			return getRuleContext(InitializerContext.class,0);
		}
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public TerminalNode SemiColon() { return getToken(TypeScriptParser.SemiColon, 0); }
		public VariableDeclarationListContext variableDeclarationList() {
			return getRuleContext(VariableDeclarationListContext.class,0);
		}
		public AccessibilityModifierContext accessibilityModifier() {
			return getRuleContext(AccessibilityModifierContext.class,0);
		}
		public VarModifierContext varModifier() {
			return getRuleContext(VarModifierContext.class,0);
		}
		public TerminalNode ReadOnly() { return getToken(TypeScriptParser.ReadOnly, 0); }
		public TerminalNode Declare() { return getToken(TypeScriptParser.Declare, 0); }
		public VariableStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterVariableStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitVariableStatement(this);
		}
	}

	public final VariableStatementContext variableStatement() throws RecognitionException {
		VariableStatementContext _localctx = new VariableStatementContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_variableStatement);
		int _la;
		try {
			setState(882);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(853);
				bindingPattern();
				setState(855);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Colon) {
					{
					setState(854);
					typeAnnotation();
					}
				}

				setState(857);
				initializer();
				setState(859);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,90,_ctx) ) {
				case 1:
					{
					setState(858);
					match(SemiColon);
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(862);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 101)) & ~0x3f) == 0 && ((1L << (_la - 101)) & 19L) != 0)) {
					{
					setState(861);
					accessibilityModifier();
					}
				}

				setState(865);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & 2281701377L) != 0)) {
					{
					setState(864);
					varModifier();
					}
				}

				setState(868);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ReadOnly) {
					{
					setState(867);
					match(ReadOnly);
					}
				}

				setState(870);
				variableDeclarationList();
				setState(872);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,94,_ctx) ) {
				case 1:
					{
					setState(871);
					match(SemiColon);
					}
					break;
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(874);
				match(Declare);
				setState(876);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & 2281701377L) != 0)) {
					{
					setState(875);
					varModifier();
					}
				}

				setState(878);
				variableDeclarationList();
				setState(880);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,96,_ctx) ) {
				case 1:
					{
					setState(879);
					match(SemiColon);
					}
					break;
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
	public static class VariableDeclarationListContext extends ParserRuleContext {
		public List<VariableDeclarationContext> variableDeclaration() {
			return getRuleContexts(VariableDeclarationContext.class);
		}
		public VariableDeclarationContext variableDeclaration(int i) {
			return getRuleContext(VariableDeclarationContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public VariableDeclarationListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclarationList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterVariableDeclarationList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitVariableDeclarationList(this);
		}
	}

	public final VariableDeclarationListContext variableDeclarationList() throws RecognitionException {
		VariableDeclarationListContext _localctx = new VariableDeclarationListContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_variableDeclarationList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(884);
			variableDeclaration();
			setState(889);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,98,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(885);
					match(Comma);
					setState(886);
					variableDeclaration();
					}
					} 
				}
				setState(891);
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
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VariableDeclarationContext extends ParserRuleContext {
		public IdentifierOrKeyWordContext identifierOrKeyWord() {
			return getRuleContext(IdentifierOrKeyWordContext.class,0);
		}
		public ArrayLiteralContext arrayLiteral() {
			return getRuleContext(ArrayLiteralContext.class,0);
		}
		public ObjectLiteralContext objectLiteral() {
			return getRuleContext(ObjectLiteralContext.class,0);
		}
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode Assign() { return getToken(TypeScriptParser.Assign, 0); }
		public TypeParametersContext typeParameters() {
			return getRuleContext(TypeParametersContext.class,0);
		}
		public VariableDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterVariableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitVariableDeclaration(this);
		}
	}

	public final VariableDeclarationContext variableDeclaration() throws RecognitionException {
		VariableDeclarationContext _localctx = new VariableDeclarationContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_variableDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(895);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TypeAlias:
			case Require:
			case Identifier:
				{
				setState(892);
				identifierOrKeyWord();
				}
				break;
			case OpenBracket:
				{
				setState(893);
				arrayLiteral();
				}
				break;
			case OpenBrace:
				{
				setState(894);
				objectLiteral();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(898);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,100,_ctx) ) {
			case 1:
				{
				setState(897);
				typeAnnotation();
				}
				break;
			}
			setState(901);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,101,_ctx) ) {
			case 1:
				{
				setState(900);
				singleExpression(0);
				}
				break;
			}
			setState(908);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,103,_ctx) ) {
			case 1:
				{
				setState(903);
				match(Assign);
				setState(905);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,102,_ctx) ) {
				case 1:
					{
					setState(904);
					typeParameters();
					}
					break;
				}
				setState(907);
				singleExpression(0);
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
	public static class EmptyStatement_Context extends ParserRuleContext {
		public TerminalNode SemiColon() { return getToken(TypeScriptParser.SemiColon, 0); }
		public EmptyStatement_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_emptyStatement_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterEmptyStatement_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitEmptyStatement_(this);
		}
	}

	public final EmptyStatement_Context emptyStatement_() throws RecognitionException {
		EmptyStatement_Context _localctx = new EmptyStatement_Context(_ctx, getState());
		enterRule(_localctx, 144, RULE_emptyStatement_);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(910);
			match(SemiColon);
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
	public static class ExpressionStatementContext extends ParserRuleContext {
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public TerminalNode SemiColon() { return getToken(TypeScriptParser.SemiColon, 0); }
		public ExpressionStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterExpressionStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitExpressionStatement(this);
		}
	}

	public final ExpressionStatementContext expressionStatement() throws RecognitionException {
		ExpressionStatementContext _localctx = new ExpressionStatementContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_expressionStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(912);
			if (!(this.notOpenBraceAndNotFunction())) throw new FailedPredicateException(this, "this.notOpenBraceAndNotFunction()");
			setState(913);
			expressionSequence();
			setState(915);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
			case 1:
				{
				setState(914);
				match(SemiColon);
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
	public static class IfStatementContext extends ParserRuleContext {
		public TerminalNode If() { return getToken(TypeScriptParser.If, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TerminalNode Else() { return getToken(TypeScriptParser.Else, 0); }
		public IfStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterIfStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitIfStatement(this);
		}
	}

	public final IfStatementContext ifStatement() throws RecognitionException {
		IfStatementContext _localctx = new IfStatementContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_ifStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(917);
			match(If);
			setState(918);
			match(OpenParen);
			setState(919);
			expressionSequence();
			setState(920);
			match(CloseParen);
			setState(921);
			statement();
			setState(924);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,105,_ctx) ) {
			case 1:
				{
				setState(922);
				match(Else);
				setState(923);
				statement();
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
	public static class IterationStatementContext extends ParserRuleContext {
		public IterationStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_iterationStatement; }
	 
		public IterationStatementContext() { }
		public void copyFrom(IterationStatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DoStatementContext extends IterationStatementContext {
		public TerminalNode Do() { return getToken(TypeScriptParser.Do, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public TerminalNode While() { return getToken(TypeScriptParser.While, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public DoStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterDoStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitDoStatement(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ForVarStatementContext extends IterationStatementContext {
		public TerminalNode For() { return getToken(TypeScriptParser.For, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public VarModifierContext varModifier() {
			return getRuleContext(VarModifierContext.class,0);
		}
		public VariableDeclarationListContext variableDeclarationList() {
			return getRuleContext(VariableDeclarationListContext.class,0);
		}
		public List<TerminalNode> SemiColon() { return getTokens(TypeScriptParser.SemiColon); }
		public TerminalNode SemiColon(int i) {
			return getToken(TypeScriptParser.SemiColon, i);
		}
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public List<ExpressionSequenceContext> expressionSequence() {
			return getRuleContexts(ExpressionSequenceContext.class);
		}
		public ExpressionSequenceContext expressionSequence(int i) {
			return getRuleContext(ExpressionSequenceContext.class,i);
		}
		public ForVarStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterForVarStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitForVarStatement(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ForVarInStatementContext extends IterationStatementContext {
		public TerminalNode For() { return getToken(TypeScriptParser.For, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public VarModifierContext varModifier() {
			return getRuleContext(VarModifierContext.class,0);
		}
		public VariableDeclarationContext variableDeclaration() {
			return getRuleContext(VariableDeclarationContext.class,0);
		}
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public TerminalNode In() { return getToken(TypeScriptParser.In, 0); }
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public ForVarInStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterForVarInStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitForVarInStatement(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class WhileStatementContext extends IterationStatementContext {
		public TerminalNode While() { return getToken(TypeScriptParser.While, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public WhileStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterWhileStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitWhileStatement(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ForStatementContext extends IterationStatementContext {
		public TerminalNode For() { return getToken(TypeScriptParser.For, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public List<TerminalNode> SemiColon() { return getTokens(TypeScriptParser.SemiColon); }
		public TerminalNode SemiColon(int i) {
			return getToken(TypeScriptParser.SemiColon, i);
		}
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public List<ExpressionSequenceContext> expressionSequence() {
			return getRuleContexts(ExpressionSequenceContext.class);
		}
		public ExpressionSequenceContext expressionSequence(int i) {
			return getRuleContext(ExpressionSequenceContext.class,i);
		}
		public ForStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterForStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitForStatement(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ForInStatementContext extends IterationStatementContext {
		public TerminalNode For() { return getToken(TypeScriptParser.For, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public TerminalNode In() { return getToken(TypeScriptParser.In, 0); }
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public ForInStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterForInStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitForInStatement(this);
		}
	}

	public final IterationStatementContext iterationStatement() throws RecognitionException {
		IterationStatementContext _localctx = new IterationStatementContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_iterationStatement);
		int _la;
		try {
			setState(995);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,113,_ctx) ) {
			case 1:
				_localctx = new DoStatementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(926);
				match(Do);
				setState(927);
				statement();
				setState(928);
				match(While);
				setState(929);
				match(OpenParen);
				setState(930);
				expressionSequence();
				setState(931);
				match(CloseParen);
				setState(932);
				eos();
				}
				break;
			case 2:
				_localctx = new WhileStatementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(934);
				match(While);
				setState(935);
				match(OpenParen);
				setState(936);
				expressionSequence();
				setState(937);
				match(CloseParen);
				setState(938);
				statement();
				}
				break;
			case 3:
				_localctx = new ForStatementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(940);
				match(For);
				setState(941);
				match(OpenParen);
				setState(943);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -36028795928706728L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 8128698260224212991L) != 0)) {
					{
					setState(942);
					expressionSequence();
					}
				}

				setState(945);
				match(SemiColon);
				setState(947);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -36028795928706728L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 8128698260224212991L) != 0)) {
					{
					setState(946);
					expressionSequence();
					}
				}

				setState(949);
				match(SemiColon);
				setState(951);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -36028795928706728L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 8128698260224212991L) != 0)) {
					{
					setState(950);
					expressionSequence();
					}
				}

				setState(953);
				match(CloseParen);
				setState(954);
				statement();
				}
				break;
			case 4:
				_localctx = new ForVarStatementContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(955);
				match(For);
				setState(956);
				match(OpenParen);
				setState(957);
				varModifier();
				setState(958);
				variableDeclarationList();
				setState(959);
				match(SemiColon);
				setState(961);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -36028795928706728L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 8128698260224212991L) != 0)) {
					{
					setState(960);
					expressionSequence();
					}
				}

				setState(963);
				match(SemiColon);
				setState(965);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -36028795928706728L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 8128698260224212991L) != 0)) {
					{
					setState(964);
					expressionSequence();
					}
				}

				setState(967);
				match(CloseParen);
				setState(968);
				statement();
				}
				break;
			case 5:
				_localctx = new ForInStatementContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(970);
				match(For);
				setState(971);
				match(OpenParen);
				setState(972);
				singleExpression(0);
				setState(976);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case In:
					{
					setState(973);
					match(In);
					}
					break;
				case Identifier:
					{
					setState(974);
					match(Identifier);
					setState(975);
					if (!(this.p("of"))) throw new FailedPredicateException(this, "this.p(\"of\")");
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(978);
				expressionSequence();
				setState(979);
				match(CloseParen);
				setState(980);
				statement();
				}
				break;
			case 6:
				_localctx = new ForVarInStatementContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(982);
				match(For);
				setState(983);
				match(OpenParen);
				setState(984);
				varModifier();
				setState(985);
				variableDeclaration();
				setState(989);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case In:
					{
					setState(986);
					match(In);
					}
					break;
				case Identifier:
					{
					setState(987);
					match(Identifier);
					setState(988);
					if (!(this.p("of"))) throw new FailedPredicateException(this, "this.p(\"of\")");
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(991);
				expressionSequence();
				setState(992);
				match(CloseParen);
				setState(993);
				statement();
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
	public static class VarModifierContext extends ParserRuleContext {
		public TerminalNode Var() { return getToken(TypeScriptParser.Var, 0); }
		public TerminalNode Let() { return getToken(TypeScriptParser.Let, 0); }
		public TerminalNode Const() { return getToken(TypeScriptParser.Const, 0); }
		public VarModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterVarModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitVarModifier(this);
		}
	}

	public final VarModifierContext varModifier() throws RecognitionException {
		VarModifierContext _localctx = new VarModifierContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_varModifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(997);
			_la = _input.LA(1);
			if ( !(((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & 2281701377L) != 0)) ) {
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
	public static class ContinueStatementContext extends ParserRuleContext {
		public TerminalNode Continue() { return getToken(TypeScriptParser.Continue, 0); }
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public ContinueStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continueStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterContinueStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitContinueStatement(this);
		}
	}

	public final ContinueStatementContext continueStatement() throws RecognitionException {
		ContinueStatementContext _localctx = new ContinueStatementContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_continueStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(999);
			match(Continue);
			setState(1002);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,114,_ctx) ) {
			case 1:
				{
				setState(1000);
				if (!(this.notLineTerminator())) throw new FailedPredicateException(this, "this.notLineTerminator()");
				setState(1001);
				match(Identifier);
				}
				break;
			}
			setState(1004);
			eos();
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
	public static class BreakStatementContext extends ParserRuleContext {
		public TerminalNode Break() { return getToken(TypeScriptParser.Break, 0); }
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public BreakStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterBreakStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitBreakStatement(this);
		}
	}

	public final BreakStatementContext breakStatement() throws RecognitionException {
		BreakStatementContext _localctx = new BreakStatementContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_breakStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1006);
			match(Break);
			setState(1009);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,115,_ctx) ) {
			case 1:
				{
				setState(1007);
				if (!(this.notLineTerminator())) throw new FailedPredicateException(this, "this.notLineTerminator()");
				setState(1008);
				match(Identifier);
				}
				break;
			}
			setState(1011);
			eos();
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
	public static class ReturnStatementContext extends ParserRuleContext {
		public TerminalNode Return() { return getToken(TypeScriptParser.Return, 0); }
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public ReturnStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterReturnStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitReturnStatement(this);
		}
	}

	public final ReturnStatementContext returnStatement() throws RecognitionException {
		ReturnStatementContext _localctx = new ReturnStatementContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_returnStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1013);
			match(Return);
			setState(1016);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,116,_ctx) ) {
			case 1:
				{
				setState(1014);
				if (!(this.notLineTerminator())) throw new FailedPredicateException(this, "this.notLineTerminator()");
				setState(1015);
				expressionSequence();
				}
				break;
			}
			setState(1018);
			eos();
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
	public static class YieldStatementContext extends ParserRuleContext {
		public TerminalNode Yield() { return getToken(TypeScriptParser.Yield, 0); }
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public YieldStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_yieldStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterYieldStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitYieldStatement(this);
		}
	}

	public final YieldStatementContext yieldStatement() throws RecognitionException {
		YieldStatementContext _localctx = new YieldStatementContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_yieldStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1020);
			match(Yield);
			setState(1023);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,117,_ctx) ) {
			case 1:
				{
				setState(1021);
				if (!(this.notLineTerminator())) throw new FailedPredicateException(this, "this.notLineTerminator()");
				setState(1022);
				expressionSequence();
				}
				break;
			}
			setState(1025);
			eos();
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
	public static class WithStatementContext extends ParserRuleContext {
		public TerminalNode With() { return getToken(TypeScriptParser.With, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public WithStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_withStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterWithStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitWithStatement(this);
		}
	}

	public final WithStatementContext withStatement() throws RecognitionException {
		WithStatementContext _localctx = new WithStatementContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_withStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1027);
			match(With);
			setState(1028);
			match(OpenParen);
			setState(1029);
			expressionSequence();
			setState(1030);
			match(CloseParen);
			setState(1031);
			statement();
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
	public static class SwitchStatementContext extends ParserRuleContext {
		public TerminalNode Switch() { return getToken(TypeScriptParser.Switch, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public CaseBlockContext caseBlock() {
			return getRuleContext(CaseBlockContext.class,0);
		}
		public SwitchStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_switchStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterSwitchStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitSwitchStatement(this);
		}
	}

	public final SwitchStatementContext switchStatement() throws RecognitionException {
		SwitchStatementContext _localctx = new SwitchStatementContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_switchStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1033);
			match(Switch);
			setState(1034);
			match(OpenParen);
			setState(1035);
			expressionSequence();
			setState(1036);
			match(CloseParen);
			setState(1037);
			caseBlock();
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
	public static class CaseBlockContext extends ParserRuleContext {
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public List<CaseClausesContext> caseClauses() {
			return getRuleContexts(CaseClausesContext.class);
		}
		public CaseClausesContext caseClauses(int i) {
			return getRuleContext(CaseClausesContext.class,i);
		}
		public DefaultClauseContext defaultClause() {
			return getRuleContext(DefaultClauseContext.class,0);
		}
		public CaseBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterCaseBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitCaseBlock(this);
		}
	}

	public final CaseBlockContext caseBlock() throws RecognitionException {
		CaseBlockContext _localctx = new CaseBlockContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_caseBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1039);
			match(OpenBrace);
			setState(1041);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Case) {
				{
				setState(1040);
				caseClauses();
				}
			}

			setState(1047);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Default) {
				{
				setState(1043);
				defaultClause();
				setState(1045);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Case) {
					{
					setState(1044);
					caseClauses();
					}
				}

				}
			}

			setState(1049);
			match(CloseBrace);
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
	public static class CaseClausesContext extends ParserRuleContext {
		public List<CaseClauseContext> caseClause() {
			return getRuleContexts(CaseClauseContext.class);
		}
		public CaseClauseContext caseClause(int i) {
			return getRuleContext(CaseClauseContext.class,i);
		}
		public CaseClausesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseClauses; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterCaseClauses(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitCaseClauses(this);
		}
	}

	public final CaseClausesContext caseClauses() throws RecognitionException {
		CaseClausesContext _localctx = new CaseClausesContext(_ctx, getState());
		enterRule(_localctx, 168, RULE_caseClauses);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1052); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(1051);
				caseClause();
				}
				}
				setState(1054); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==Case );
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
	public static class CaseClauseContext extends ParserRuleContext {
		public TerminalNode Case() { return getToken(TypeScriptParser.Case, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public TerminalNode Colon() { return getToken(TypeScriptParser.Colon, 0); }
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public CaseClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterCaseClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitCaseClause(this);
		}
	}

	public final CaseClauseContext caseClause() throws RecognitionException {
		CaseClauseContext _localctx = new CaseClauseContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_caseClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1056);
			match(Case);
			setState(1057);
			expressionSequence();
			setState(1058);
			match(Colon);
			setState(1060);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,122,_ctx) ) {
			case 1:
				{
				setState(1059);
				statementList();
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
	public static class DefaultClauseContext extends ParserRuleContext {
		public TerminalNode Default() { return getToken(TypeScriptParser.Default, 0); }
		public TerminalNode Colon() { return getToken(TypeScriptParser.Colon, 0); }
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public DefaultClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defaultClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterDefaultClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitDefaultClause(this);
		}
	}

	public final DefaultClauseContext defaultClause() throws RecognitionException {
		DefaultClauseContext _localctx = new DefaultClauseContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_defaultClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1062);
			match(Default);
			setState(1063);
			match(Colon);
			setState(1065);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,123,_ctx) ) {
			case 1:
				{
				setState(1064);
				statementList();
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
	public static class LabelledStatementContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public TerminalNode Colon() { return getToken(TypeScriptParser.Colon, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public LabelledStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labelledStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterLabelledStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitLabelledStatement(this);
		}
	}

	public final LabelledStatementContext labelledStatement() throws RecognitionException {
		LabelledStatementContext _localctx = new LabelledStatementContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_labelledStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1067);
			match(Identifier);
			setState(1068);
			match(Colon);
			setState(1069);
			statement();
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
	public static class ThrowStatementContext extends ParserRuleContext {
		public TerminalNode Throw() { return getToken(TypeScriptParser.Throw, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public ThrowStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_throwStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterThrowStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitThrowStatement(this);
		}
	}

	public final ThrowStatementContext throwStatement() throws RecognitionException {
		ThrowStatementContext _localctx = new ThrowStatementContext(_ctx, getState());
		enterRule(_localctx, 176, RULE_throwStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1071);
			match(Throw);
			setState(1072);
			if (!(this.notLineTerminator())) throw new FailedPredicateException(this, "this.notLineTerminator()");
			setState(1073);
			expressionSequence();
			setState(1074);
			eos();
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
	public static class TryStatementContext extends ParserRuleContext {
		public TerminalNode Try() { return getToken(TypeScriptParser.Try, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public CatchProductionContext catchProduction() {
			return getRuleContext(CatchProductionContext.class,0);
		}
		public FinallyProductionContext finallyProduction() {
			return getRuleContext(FinallyProductionContext.class,0);
		}
		public TryStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tryStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTryStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTryStatement(this);
		}
	}

	public final TryStatementContext tryStatement() throws RecognitionException {
		TryStatementContext _localctx = new TryStatementContext(_ctx, getState());
		enterRule(_localctx, 178, RULE_tryStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1076);
			match(Try);
			setState(1077);
			block();
			setState(1083);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Catch:
				{
				setState(1078);
				catchProduction();
				setState(1080);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,124,_ctx) ) {
				case 1:
					{
					setState(1079);
					finallyProduction();
					}
					break;
				}
				}
				break;
			case Finally:
				{
				setState(1082);
				finallyProduction();
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
	public static class CatchProductionContext extends ParserRuleContext {
		public TerminalNode Catch() { return getToken(TypeScriptParser.Catch, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public CatchProductionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_catchProduction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterCatchProduction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitCatchProduction(this);
		}
	}

	public final CatchProductionContext catchProduction() throws RecognitionException {
		CatchProductionContext _localctx = new CatchProductionContext(_ctx, getState());
		enterRule(_localctx, 180, RULE_catchProduction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1085);
			match(Catch);
			setState(1086);
			match(OpenParen);
			setState(1087);
			match(Identifier);
			setState(1088);
			match(CloseParen);
			setState(1089);
			block();
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
	public static class FinallyProductionContext extends ParserRuleContext {
		public TerminalNode Finally() { return getToken(TypeScriptParser.Finally, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public FinallyProductionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_finallyProduction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterFinallyProduction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitFinallyProduction(this);
		}
	}

	public final FinallyProductionContext finallyProduction() throws RecognitionException {
		FinallyProductionContext _localctx = new FinallyProductionContext(_ctx, getState());
		enterRule(_localctx, 182, RULE_finallyProduction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1091);
			match(Finally);
			setState(1092);
			block();
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
	public static class DebuggerStatementContext extends ParserRuleContext {
		public TerminalNode Debugger() { return getToken(TypeScriptParser.Debugger, 0); }
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public DebuggerStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_debuggerStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterDebuggerStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitDebuggerStatement(this);
		}
	}

	public final DebuggerStatementContext debuggerStatement() throws RecognitionException {
		DebuggerStatementContext _localctx = new DebuggerStatementContext(_ctx, getState());
		enterRule(_localctx, 184, RULE_debuggerStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1094);
			match(Debugger);
			setState(1095);
			eos();
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
	public static class FunctionDeclarationContext extends ParserRuleContext {
		public TerminalNode Function_() { return getToken(TypeScriptParser.Function_, 0); }
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public CallSignatureContext callSignature() {
			return getRuleContext(CallSignatureContext.class,0);
		}
		public TerminalNode SemiColon() { return getToken(TypeScriptParser.SemiColon, 0); }
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public FunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterFunctionDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitFunctionDeclaration(this);
		}
	}

	public final FunctionDeclarationContext functionDeclaration() throws RecognitionException {
		FunctionDeclarationContext _localctx = new FunctionDeclarationContext(_ctx, getState());
		enterRule(_localctx, 186, RULE_functionDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1097);
			match(Function_);
			setState(1098);
			match(Identifier);
			setState(1099);
			callSignature();
			setState(1105);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OpenBrace:
				{
				{
				setState(1100);
				match(OpenBrace);
				setState(1101);
				functionBody();
				setState(1102);
				match(CloseBrace);
				}
				}
				break;
			case SemiColon:
				{
				setState(1104);
				match(SemiColon);
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
	public static class ClassDeclarationContext extends ParserRuleContext {
		public TerminalNode Class() { return getToken(TypeScriptParser.Class, 0); }
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public ClassHeritageContext classHeritage() {
			return getRuleContext(ClassHeritageContext.class,0);
		}
		public ClassTailContext classTail() {
			return getRuleContext(ClassTailContext.class,0);
		}
		public DecoratorListContext decoratorList() {
			return getRuleContext(DecoratorListContext.class,0);
		}
		public TerminalNode Export() { return getToken(TypeScriptParser.Export, 0); }
		public TerminalNode Abstract() { return getToken(TypeScriptParser.Abstract, 0); }
		public TypeParametersContext typeParameters() {
			return getRuleContext(TypeParametersContext.class,0);
		}
		public TerminalNode Default() { return getToken(TypeScriptParser.Default, 0); }
		public ClassDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterClassDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitClassDeclaration(this);
		}
	}

	public final ClassDeclarationContext classDeclaration() throws RecognitionException {
		ClassDeclarationContext _localctx = new ClassDeclarationContext(_ctx, getState());
		enterRule(_localctx, 188, RULE_classDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1108);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==At) {
				{
				setState(1107);
				decoratorList();
				}
			}

			setState(1114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Export) {
				{
				setState(1110);
				match(Export);
				setState(1112);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Default) {
					{
					setState(1111);
					match(Default);
					}
				}

				}
			}

			setState(1117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Abstract) {
				{
				setState(1116);
				match(Abstract);
				}
			}

			setState(1119);
			match(Class);
			setState(1120);
			match(Identifier);
			setState(1122);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LessThan) {
				{
				setState(1121);
				typeParameters();
				}
			}

			setState(1124);
			classHeritage();
			setState(1125);
			classTail();
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
	public static class ClassHeritageContext extends ParserRuleContext {
		public ClassExtendsClauseContext classExtendsClause() {
			return getRuleContext(ClassExtendsClauseContext.class,0);
		}
		public ImplementsClauseContext implementsClause() {
			return getRuleContext(ImplementsClauseContext.class,0);
		}
		public ClassHeritageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classHeritage; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterClassHeritage(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitClassHeritage(this);
		}
	}

	public final ClassHeritageContext classHeritage() throws RecognitionException {
		ClassHeritageContext _localctx = new ClassHeritageContext(_ctx, getState());
		enterRule(_localctx, 190, RULE_classHeritage);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1128);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Extends) {
				{
				setState(1127);
				classExtendsClause();
				}
			}

			setState(1131);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Implements) {
				{
				setState(1130);
				implementsClause();
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
	public static class ClassTailContext extends ParserRuleContext {
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public List<ClassElementContext> classElement() {
			return getRuleContexts(ClassElementContext.class);
		}
		public ClassElementContext classElement(int i) {
			return getRuleContext(ClassElementContext.class,i);
		}
		public ClassTailContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classTail; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterClassTail(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitClassTail(this);
		}
	}

	public final ClassTailContext classTail() throws RecognitionException {
		ClassTailContext _localctx = new ClassTailContext(_ctx, getState());
		enterRule(_localctx, 192, RULE_classTail);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1133);
			match(OpenBrace);
			setState(1137);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,134,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1134);
					classElement();
					}
					} 
				}
				setState(1139);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,134,_ctx);
			}
			setState(1140);
			match(CloseBrace);
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
	public static class ClassExtendsClauseContext extends ParserRuleContext {
		public TerminalNode Extends() { return getToken(TypeScriptParser.Extends, 0); }
		public TypeReferenceContext typeReference() {
			return getRuleContext(TypeReferenceContext.class,0);
		}
		public ClassExtendsClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classExtendsClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterClassExtendsClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitClassExtendsClause(this);
		}
	}

	public final ClassExtendsClauseContext classExtendsClause() throws RecognitionException {
		ClassExtendsClauseContext _localctx = new ClassExtendsClauseContext(_ctx, getState());
		enterRule(_localctx, 194, RULE_classExtendsClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1142);
			match(Extends);
			setState(1143);
			typeReference();
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
	public static class ImplementsClauseContext extends ParserRuleContext {
		public TerminalNode Implements() { return getToken(TypeScriptParser.Implements, 0); }
		public ClassOrInterfaceTypeListContext classOrInterfaceTypeList() {
			return getRuleContext(ClassOrInterfaceTypeListContext.class,0);
		}
		public ImplementsClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_implementsClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterImplementsClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitImplementsClause(this);
		}
	}

	public final ImplementsClauseContext implementsClause() throws RecognitionException {
		ImplementsClauseContext _localctx = new ImplementsClauseContext(_ctx, getState());
		enterRule(_localctx, 196, RULE_implementsClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1145);
			match(Implements);
			setState(1146);
			classOrInterfaceTypeList();
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
	public static class ClassElementContext extends ParserRuleContext {
		public ConstructorDeclarationContext constructorDeclaration() {
			return getRuleContext(ConstructorDeclarationContext.class,0);
		}
		public PropertyMemberDeclarationContext propertyMemberDeclaration() {
			return getRuleContext(PropertyMemberDeclarationContext.class,0);
		}
		public DecoratorListContext decoratorList() {
			return getRuleContext(DecoratorListContext.class,0);
		}
		public IndexMemberDeclarationContext indexMemberDeclaration() {
			return getRuleContext(IndexMemberDeclarationContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ClassElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterClassElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitClassElement(this);
		}
	}

	public final ClassElementContext classElement() throws RecognitionException {
		ClassElementContext _localctx = new ClassElementContext(_ctx, getState());
		enterRule(_localctx, 198, RULE_classElement);
		int _la;
		try {
			setState(1155);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,136,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1148);
				constructorDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1150);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==At) {
					{
					setState(1149);
					decoratorList();
					}
				}

				setState(1152);
				propertyMemberDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1153);
				indexMemberDeclaration();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1154);
				statement();
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
	public static class PropertyMemberDeclarationContext extends ParserRuleContext {
		public PropertyMemberDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_propertyMemberDeclaration; }
	 
		public PropertyMemberDeclarationContext() { }
		public void copyFrom(PropertyMemberDeclarationContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PropertyDeclarationExpressionContext extends PropertyMemberDeclarationContext {
		public PropertyMemberBaseContext propertyMemberBase() {
			return getRuleContext(PropertyMemberBaseContext.class,0);
		}
		public PropertyNameContext propertyName() {
			return getRuleContext(PropertyNameContext.class,0);
		}
		public TerminalNode SemiColon() { return getToken(TypeScriptParser.SemiColon, 0); }
		public TerminalNode QuestionMark() { return getToken(TypeScriptParser.QuestionMark, 0); }
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public InitializerContext initializer() {
			return getRuleContext(InitializerContext.class,0);
		}
		public PropertyDeclarationExpressionContext(PropertyMemberDeclarationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterPropertyDeclarationExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitPropertyDeclarationExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MethodDeclarationExpressionContext extends PropertyMemberDeclarationContext {
		public PropertyMemberBaseContext propertyMemberBase() {
			return getRuleContext(PropertyMemberBaseContext.class,0);
		}
		public PropertyNameContext propertyName() {
			return getRuleContext(PropertyNameContext.class,0);
		}
		public CallSignatureContext callSignature() {
			return getRuleContext(CallSignatureContext.class,0);
		}
		public TerminalNode SemiColon() { return getToken(TypeScriptParser.SemiColon, 0); }
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public MethodDeclarationExpressionContext(PropertyMemberDeclarationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterMethodDeclarationExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitMethodDeclarationExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class GetterSetterDeclarationExpressionContext extends PropertyMemberDeclarationContext {
		public PropertyMemberBaseContext propertyMemberBase() {
			return getRuleContext(PropertyMemberBaseContext.class,0);
		}
		public GetAccessorContext getAccessor() {
			return getRuleContext(GetAccessorContext.class,0);
		}
		public SetAccessorContext setAccessor() {
			return getRuleContext(SetAccessorContext.class,0);
		}
		public GetterSetterDeclarationExpressionContext(PropertyMemberDeclarationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterGetterSetterDeclarationExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitGetterSetterDeclarationExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AbstractMemberDeclarationContext extends PropertyMemberDeclarationContext {
		public AbstractDeclarationContext abstractDeclaration() {
			return getRuleContext(AbstractDeclarationContext.class,0);
		}
		public AbstractMemberDeclarationContext(PropertyMemberDeclarationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterAbstractMemberDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitAbstractMemberDeclaration(this);
		}
	}

	public final PropertyMemberDeclarationContext propertyMemberDeclaration() throws RecognitionException {
		PropertyMemberDeclarationContext _localctx = new PropertyMemberDeclarationContext(_ctx, getState());
		enterRule(_localctx, 200, RULE_propertyMemberDeclaration);
		int _la;
		try {
			setState(1186);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,142,_ctx) ) {
			case 1:
				_localctx = new PropertyDeclarationExpressionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1157);
				propertyMemberBase();
				setState(1158);
				propertyName();
				setState(1160);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==QuestionMark) {
					{
					setState(1159);
					match(QuestionMark);
					}
				}

				setState(1163);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Colon) {
					{
					setState(1162);
					typeAnnotation();
					}
				}

				setState(1166);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Assign) {
					{
					setState(1165);
					initializer();
					}
				}

				setState(1168);
				match(SemiColon);
				}
				break;
			case 2:
				_localctx = new MethodDeclarationExpressionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1170);
				propertyMemberBase();
				setState(1171);
				propertyName();
				setState(1172);
				callSignature();
				setState(1178);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case OpenBrace:
					{
					{
					setState(1173);
					match(OpenBrace);
					setState(1174);
					functionBody();
					setState(1175);
					match(CloseBrace);
					}
					}
					break;
				case SemiColon:
					{
					setState(1177);
					match(SemiColon);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 3:
				_localctx = new GetterSetterDeclarationExpressionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(1180);
				propertyMemberBase();
				setState(1183);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case Get:
					{
					setState(1181);
					getAccessor();
					}
					break;
				case Set:
					{
					setState(1182);
					setAccessor();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 4:
				_localctx = new AbstractMemberDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(1185);
				abstractDeclaration();
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
	public static class PropertyMemberBaseContext extends ParserRuleContext {
		public AccessibilityModifierContext accessibilityModifier() {
			return getRuleContext(AccessibilityModifierContext.class,0);
		}
		public TerminalNode Async() { return getToken(TypeScriptParser.Async, 0); }
		public TerminalNode Static() { return getToken(TypeScriptParser.Static, 0); }
		public TerminalNode ReadOnly() { return getToken(TypeScriptParser.ReadOnly, 0); }
		public PropertyMemberBaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_propertyMemberBase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterPropertyMemberBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitPropertyMemberBase(this);
		}
	}

	public final PropertyMemberBaseContext propertyMemberBase() throws RecognitionException {
		PropertyMemberBaseContext _localctx = new PropertyMemberBaseContext(_ctx, getState());
		enterRule(_localctx, 202, RULE_propertyMemberBase);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1189);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,143,_ctx) ) {
			case 1:
				{
				setState(1188);
				accessibilityModifier();
				}
				break;
			}
			setState(1192);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,144,_ctx) ) {
			case 1:
				{
				setState(1191);
				match(Async);
				}
				break;
			}
			setState(1195);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,145,_ctx) ) {
			case 1:
				{
				setState(1194);
				match(Static);
				}
				break;
			}
			setState(1198);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,146,_ctx) ) {
			case 1:
				{
				setState(1197);
				match(ReadOnly);
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
	public static class IndexMemberDeclarationContext extends ParserRuleContext {
		public IndexSignatureContext indexSignature() {
			return getRuleContext(IndexSignatureContext.class,0);
		}
		public TerminalNode SemiColon() { return getToken(TypeScriptParser.SemiColon, 0); }
		public IndexMemberDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_indexMemberDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterIndexMemberDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitIndexMemberDeclaration(this);
		}
	}

	public final IndexMemberDeclarationContext indexMemberDeclaration() throws RecognitionException {
		IndexMemberDeclarationContext _localctx = new IndexMemberDeclarationContext(_ctx, getState());
		enterRule(_localctx, 204, RULE_indexMemberDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1200);
			indexSignature();
			setState(1201);
			match(SemiColon);
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
	public static class GeneratorMethodContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public TerminalNode Multiply() { return getToken(TypeScriptParser.Multiply, 0); }
		public FormalParameterListContext formalParameterList() {
			return getRuleContext(FormalParameterListContext.class,0);
		}
		public GeneratorMethodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generatorMethod; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterGeneratorMethod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitGeneratorMethod(this);
		}
	}

	public final GeneratorMethodContext generatorMethod() throws RecognitionException {
		GeneratorMethodContext _localctx = new GeneratorMethodContext(_ctx, getState());
		enterRule(_localctx, 206, RULE_generatorMethod);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1204);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Multiply) {
				{
				setState(1203);
				match(Multiply);
				}
			}

			setState(1206);
			match(Identifier);
			setState(1207);
			match(OpenParen);
			setState(1209);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 65808L) != 0) || ((((_la - 101)) & ~0x3f) == 0 && ((1L << (_la - 101)) & 12718099L) != 0)) {
				{
				setState(1208);
				formalParameterList();
				}
			}

			setState(1211);
			match(CloseParen);
			setState(1212);
			match(OpenBrace);
			setState(1213);
			functionBody();
			setState(1214);
			match(CloseBrace);
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
	public static class GeneratorFunctionDeclarationContext extends ParserRuleContext {
		public TerminalNode Function_() { return getToken(TypeScriptParser.Function_, 0); }
		public TerminalNode Multiply() { return getToken(TypeScriptParser.Multiply, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public FormalParameterListContext formalParameterList() {
			return getRuleContext(FormalParameterListContext.class,0);
		}
		public GeneratorFunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generatorFunctionDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterGeneratorFunctionDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitGeneratorFunctionDeclaration(this);
		}
	}

	public final GeneratorFunctionDeclarationContext generatorFunctionDeclaration() throws RecognitionException {
		GeneratorFunctionDeclarationContext _localctx = new GeneratorFunctionDeclarationContext(_ctx, getState());
		enterRule(_localctx, 208, RULE_generatorFunctionDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1216);
			match(Function_);
			setState(1217);
			match(Multiply);
			setState(1219);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(1218);
				match(Identifier);
				}
			}

			setState(1221);
			match(OpenParen);
			setState(1223);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 65808L) != 0) || ((((_la - 101)) & ~0x3f) == 0 && ((1L << (_la - 101)) & 12718099L) != 0)) {
				{
				setState(1222);
				formalParameterList();
				}
			}

			setState(1225);
			match(CloseParen);
			setState(1226);
			match(OpenBrace);
			setState(1227);
			functionBody();
			setState(1228);
			match(CloseBrace);
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
	public static class GeneratorBlockContext extends ParserRuleContext {
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public List<GeneratorDefinitionContext> generatorDefinition() {
			return getRuleContexts(GeneratorDefinitionContext.class);
		}
		public GeneratorDefinitionContext generatorDefinition(int i) {
			return getRuleContext(GeneratorDefinitionContext.class,i);
		}
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public GeneratorBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generatorBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterGeneratorBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitGeneratorBlock(this);
		}
	}

	public final GeneratorBlockContext generatorBlock() throws RecognitionException {
		GeneratorBlockContext _localctx = new GeneratorBlockContext(_ctx, getState());
		enterRule(_localctx, 210, RULE_generatorBlock);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1230);
			match(OpenBrace);
			setState(1231);
			generatorDefinition();
			setState(1236);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,151,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1232);
					match(Comma);
					setState(1233);
					generatorDefinition();
					}
					} 
				}
				setState(1238);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,151,_ctx);
			}
			setState(1240);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Comma) {
				{
				setState(1239);
				match(Comma);
				}
			}

			setState(1242);
			match(CloseBrace);
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
	public static class GeneratorDefinitionContext extends ParserRuleContext {
		public TerminalNode Multiply() { return getToken(TypeScriptParser.Multiply, 0); }
		public IteratorDefinitionContext iteratorDefinition() {
			return getRuleContext(IteratorDefinitionContext.class,0);
		}
		public GeneratorDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generatorDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterGeneratorDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitGeneratorDefinition(this);
		}
	}

	public final GeneratorDefinitionContext generatorDefinition() throws RecognitionException {
		GeneratorDefinitionContext _localctx = new GeneratorDefinitionContext(_ctx, getState());
		enterRule(_localctx, 212, RULE_generatorDefinition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1244);
			match(Multiply);
			setState(1245);
			iteratorDefinition();
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
	public static class IteratorBlockContext extends ParserRuleContext {
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public List<IteratorDefinitionContext> iteratorDefinition() {
			return getRuleContexts(IteratorDefinitionContext.class);
		}
		public IteratorDefinitionContext iteratorDefinition(int i) {
			return getRuleContext(IteratorDefinitionContext.class,i);
		}
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public IteratorBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_iteratorBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterIteratorBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitIteratorBlock(this);
		}
	}

	public final IteratorBlockContext iteratorBlock() throws RecognitionException {
		IteratorBlockContext _localctx = new IteratorBlockContext(_ctx, getState());
		enterRule(_localctx, 214, RULE_iteratorBlock);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1247);
			match(OpenBrace);
			setState(1248);
			iteratorDefinition();
			setState(1253);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,153,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1249);
					match(Comma);
					setState(1250);
					iteratorDefinition();
					}
					} 
				}
				setState(1255);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,153,_ctx);
			}
			setState(1257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Comma) {
				{
				setState(1256);
				match(Comma);
				}
			}

			setState(1259);
			match(CloseBrace);
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
	public static class IteratorDefinitionContext extends ParserRuleContext {
		public TerminalNode OpenBracket() { return getToken(TypeScriptParser.OpenBracket, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TerminalNode CloseBracket() { return getToken(TypeScriptParser.CloseBracket, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public FormalParameterListContext formalParameterList() {
			return getRuleContext(FormalParameterListContext.class,0);
		}
		public IteratorDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_iteratorDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterIteratorDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitIteratorDefinition(this);
		}
	}

	public final IteratorDefinitionContext iteratorDefinition() throws RecognitionException {
		IteratorDefinitionContext _localctx = new IteratorDefinitionContext(_ctx, getState());
		enterRule(_localctx, 216, RULE_iteratorDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1261);
			match(OpenBracket);
			setState(1262);
			singleExpression(0);
			setState(1263);
			match(CloseBracket);
			setState(1264);
			match(OpenParen);
			setState(1266);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 65808L) != 0) || ((((_la - 101)) & ~0x3f) == 0 && ((1L << (_la - 101)) & 12718099L) != 0)) {
				{
				setState(1265);
				formalParameterList();
				}
			}

			setState(1268);
			match(CloseParen);
			setState(1269);
			match(OpenBrace);
			setState(1270);
			functionBody();
			setState(1271);
			match(CloseBrace);
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
	public static class FormalParameterListContext extends ParserRuleContext {
		public List<FormalParameterArgContext> formalParameterArg() {
			return getRuleContexts(FormalParameterArgContext.class);
		}
		public FormalParameterArgContext formalParameterArg(int i) {
			return getRuleContext(FormalParameterArgContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public LastFormalParameterArgContext lastFormalParameterArg() {
			return getRuleContext(LastFormalParameterArgContext.class,0);
		}
		public ArrayLiteralContext arrayLiteral() {
			return getRuleContext(ArrayLiteralContext.class,0);
		}
		public ObjectLiteralContext objectLiteral() {
			return getRuleContext(ObjectLiteralContext.class,0);
		}
		public TerminalNode Colon() { return getToken(TypeScriptParser.Colon, 0); }
		public FormalParameterListContext formalParameterList() {
			return getRuleContext(FormalParameterListContext.class,0);
		}
		public FormalParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterFormalParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitFormalParameterList(this);
		}
	}

	public final FormalParameterListContext formalParameterList() throws RecognitionException {
		FormalParameterListContext _localctx = new FormalParameterListContext(_ctx, getState());
		enterRule(_localctx, 218, RULE_formalParameterList);
		int _la;
		try {
			int _alt;
			setState(1292);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Private:
			case Public:
			case Protected:
			case TypeAlias:
			case Require:
			case At:
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1273);
				formalParameterArg();
				setState(1278);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,156,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1274);
						match(Comma);
						setState(1275);
						formalParameterArg();
						}
						} 
					}
					setState(1280);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,156,_ctx);
				}
				setState(1283);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(1281);
					match(Comma);
					setState(1282);
					lastFormalParameterArg();
					}
				}

				}
				break;
			case Ellipsis:
				enterOuterAlt(_localctx, 2);
				{
				setState(1285);
				lastFormalParameterArg();
				}
				break;
			case OpenBracket:
				enterOuterAlt(_localctx, 3);
				{
				setState(1286);
				arrayLiteral();
				}
				break;
			case OpenBrace:
				enterOuterAlt(_localctx, 4);
				{
				setState(1287);
				objectLiteral();
				setState(1290);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Colon) {
					{
					setState(1288);
					match(Colon);
					setState(1289);
					formalParameterList();
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

	@SuppressWarnings("CheckReturnValue")
	public static class FormalParameterArgContext extends ParserRuleContext {
		public IdentifierOrKeyWordContext identifierOrKeyWord() {
			return getRuleContext(IdentifierOrKeyWordContext.class,0);
		}
		public DecoratorContext decorator() {
			return getRuleContext(DecoratorContext.class,0);
		}
		public AccessibilityModifierContext accessibilityModifier() {
			return getRuleContext(AccessibilityModifierContext.class,0);
		}
		public TerminalNode QuestionMark() { return getToken(TypeScriptParser.QuestionMark, 0); }
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public TerminalNode Assign() { return getToken(TypeScriptParser.Assign, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public FormalParameterArgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParameterArg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterFormalParameterArg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitFormalParameterArg(this);
		}
	}

	public final FormalParameterArgContext formalParameterArg() throws RecognitionException {
		FormalParameterArgContext _localctx = new FormalParameterArgContext(_ctx, getState());
		enterRule(_localctx, 220, RULE_formalParameterArg);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1295);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==At) {
				{
				setState(1294);
				decorator();
				}
			}

			setState(1298);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 101)) & ~0x3f) == 0 && ((1L << (_la - 101)) & 19L) != 0)) {
				{
				setState(1297);
				accessibilityModifier();
				}
			}

			setState(1300);
			identifierOrKeyWord();
			setState(1302);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==QuestionMark) {
				{
				setState(1301);
				match(QuestionMark);
				}
			}

			setState(1305);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Colon) {
				{
				setState(1304);
				typeAnnotation();
				}
			}

			setState(1309);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Assign) {
				{
				setState(1307);
				match(Assign);
				setState(1308);
				singleExpression(0);
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
	public static class LastFormalParameterArgContext extends ParserRuleContext {
		public TerminalNode Ellipsis() { return getToken(TypeScriptParser.Ellipsis, 0); }
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public LastFormalParameterArgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lastFormalParameterArg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterLastFormalParameterArg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitLastFormalParameterArg(this);
		}
	}

	public final LastFormalParameterArgContext lastFormalParameterArg() throws RecognitionException {
		LastFormalParameterArgContext _localctx = new LastFormalParameterArgContext(_ctx, getState());
		enterRule(_localctx, 222, RULE_lastFormalParameterArg);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1311);
			match(Ellipsis);
			setState(1312);
			match(Identifier);
			setState(1314);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Colon) {
				{
				setState(1313);
				typeAnnotation();
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
	public static class FunctionBodyContext extends ParserRuleContext {
		public SourceElementsContext sourceElements() {
			return getRuleContext(SourceElementsContext.class,0);
		}
		public FunctionBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterFunctionBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitFunctionBody(this);
		}
	}

	public final FunctionBodyContext functionBody() throws RecognitionException {
		FunctionBodyContext _localctx = new FunctionBodyContext(_ctx, getState());
		enterRule(_localctx, 224, RULE_functionBody);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1317);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,166,_ctx) ) {
			case 1:
				{
				setState(1316);
				sourceElements();
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
	public static class SourceElementsContext extends ParserRuleContext {
		public List<SourceElementContext> sourceElement() {
			return getRuleContexts(SourceElementContext.class);
		}
		public SourceElementContext sourceElement(int i) {
			return getRuleContext(SourceElementContext.class,i);
		}
		public SourceElementsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sourceElements; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterSourceElements(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitSourceElements(this);
		}
	}

	public final SourceElementsContext sourceElements() throws RecognitionException {
		SourceElementsContext _localctx = new SourceElementsContext(_ctx, getState());
		enterRule(_localctx, 226, RULE_sourceElements);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1320); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(1319);
					sourceElement();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(1322); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,167,_ctx);
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
	public static class ArrayLiteralContext extends ParserRuleContext {
		public TerminalNode OpenBracket() { return getToken(TypeScriptParser.OpenBracket, 0); }
		public TerminalNode CloseBracket() { return getToken(TypeScriptParser.CloseBracket, 0); }
		public ElementListContext elementList() {
			return getRuleContext(ElementListContext.class,0);
		}
		public ArrayLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterArrayLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitArrayLiteral(this);
		}
	}

	public final ArrayLiteralContext arrayLiteral() throws RecognitionException {
		ArrayLiteralContext _localctx = new ArrayLiteralContext(_ctx, getState());
		enterRule(_localctx, 228, RULE_arrayLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1324);
			match(OpenBracket);
			setState(1326);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -36028795928641192L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 8128698260224212991L) != 0)) {
				{
				setState(1325);
				elementList();
				}
			}

			setState(1328);
			match(CloseBracket);
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
	public static class ElementListContext extends ParserRuleContext {
		public List<ArrayElementContext> arrayElement() {
			return getRuleContexts(ArrayElementContext.class);
		}
		public ArrayElementContext arrayElement(int i) {
			return getRuleContext(ArrayElementContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public ElementListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elementList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterElementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitElementList(this);
		}
	}

	public final ElementListContext elementList() throws RecognitionException {
		ElementListContext _localctx = new ElementListContext(_ctx, getState());
		enterRule(_localctx, 230, RULE_elementList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1330);
			arrayElement();
			setState(1339);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(1332); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(1331);
					match(Comma);
					}
					}
					setState(1334); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==Comma );
				setState(1336);
				arrayElement();
				}
				}
				setState(1341);
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
	public static class ArrayElementContext extends ParserRuleContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public TerminalNode Ellipsis() { return getToken(TypeScriptParser.Ellipsis, 0); }
		public TerminalNode Comma() { return getToken(TypeScriptParser.Comma, 0); }
		public ArrayElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterArrayElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitArrayElement(this);
		}
	}

	public final ArrayElementContext arrayElement() throws RecognitionException {
		ArrayElementContext _localctx = new ArrayElementContext(_ctx, getState());
		enterRule(_localctx, 232, RULE_arrayElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1343);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Ellipsis) {
				{
				setState(1342);
				match(Ellipsis);
				}
			}

			setState(1347);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,172,_ctx) ) {
			case 1:
				{
				setState(1345);
				singleExpression(0);
				}
				break;
			case 2:
				{
				setState(1346);
				match(Identifier);
				}
				break;
			}
			setState(1350);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,173,_ctx) ) {
			case 1:
				{
				setState(1349);
				match(Comma);
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
	public static class ObjectLiteralContext extends ParserRuleContext {
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public List<PropertyAssignmentContext> propertyAssignment() {
			return getRuleContexts(PropertyAssignmentContext.class);
		}
		public PropertyAssignmentContext propertyAssignment(int i) {
			return getRuleContext(PropertyAssignmentContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public ObjectLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterObjectLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitObjectLiteral(this);
		}
	}

	public final ObjectLiteralContext objectLiteral() throws RecognitionException {
		ObjectLiteralContext _localctx = new ObjectLiteralContext(_ctx, getState());
		enterRule(_localctx, 234, RULE_objectLiteral);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1352);
			match(OpenBrace);
			setState(1364);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -36028797002121200L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 3517012241796825087L) != 0)) {
				{
				setState(1353);
				propertyAssignment();
				setState(1358);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,174,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1354);
						match(Comma);
						setState(1355);
						propertyAssignment();
						}
						} 
					}
					setState(1360);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,174,_ctx);
				}
				setState(1362);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(1361);
					match(Comma);
					}
				}

				}
			}

			setState(1366);
			match(CloseBrace);
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
	public static class PropertyAssignmentContext extends ParserRuleContext {
		public PropertyAssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_propertyAssignment; }
	 
		public PropertyAssignmentContext() { }
		public void copyFrom(PropertyAssignmentContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PropertyExpressionAssignmentContext extends PropertyAssignmentContext {
		public PropertyNameContext propertyName() {
			return getRuleContext(PropertyNameContext.class,0);
		}
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TerminalNode Colon() { return getToken(TypeScriptParser.Colon, 0); }
		public TerminalNode Assign() { return getToken(TypeScriptParser.Assign, 0); }
		public PropertyExpressionAssignmentContext(PropertyAssignmentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterPropertyExpressionAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitPropertyExpressionAssignment(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ComputedPropertyExpressionAssignmentContext extends PropertyAssignmentContext {
		public TerminalNode OpenBracket() { return getToken(TypeScriptParser.OpenBracket, 0); }
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode CloseBracket() { return getToken(TypeScriptParser.CloseBracket, 0); }
		public TerminalNode Colon() { return getToken(TypeScriptParser.Colon, 0); }
		public ComputedPropertyExpressionAssignmentContext(PropertyAssignmentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterComputedPropertyExpressionAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitComputedPropertyExpressionAssignment(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PropertyShorthandContext extends PropertyAssignmentContext {
		public IdentifierOrKeyWordContext identifierOrKeyWord() {
			return getRuleContext(IdentifierOrKeyWordContext.class,0);
		}
		public PropertyShorthandContext(PropertyAssignmentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterPropertyShorthand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitPropertyShorthand(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PropertySetterContext extends PropertyAssignmentContext {
		public SetAccessorContext setAccessor() {
			return getRuleContext(SetAccessorContext.class,0);
		}
		public PropertySetterContext(PropertyAssignmentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterPropertySetter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitPropertySetter(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PropertyGetterContext extends PropertyAssignmentContext {
		public GetAccessorContext getAccessor() {
			return getRuleContext(GetAccessorContext.class,0);
		}
		public PropertyGetterContext(PropertyAssignmentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterPropertyGetter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitPropertyGetter(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RestParameterInObjectContext extends PropertyAssignmentContext {
		public RestParameterContext restParameter() {
			return getRuleContext(RestParameterContext.class,0);
		}
		public RestParameterInObjectContext(PropertyAssignmentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterRestParameterInObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitRestParameterInObject(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MethodPropertyContext extends PropertyAssignmentContext {
		public GeneratorMethodContext generatorMethod() {
			return getRuleContext(GeneratorMethodContext.class,0);
		}
		public MethodPropertyContext(PropertyAssignmentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterMethodProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitMethodProperty(this);
		}
	}

	public final PropertyAssignmentContext propertyAssignment() throws RecognitionException {
		PropertyAssignmentContext _localctx = new PropertyAssignmentContext(_ctx, getState());
		enterRule(_localctx, 236, RULE_propertyAssignment);
		int _la;
		try {
			setState(1383);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,177,_ctx) ) {
			case 1:
				_localctx = new PropertyExpressionAssignmentContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1368);
				propertyName();
				setState(1369);
				_la = _input.LA(1);
				if ( !(_la==Assign || _la==Colon) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1370);
				singleExpression(0);
				}
				break;
			case 2:
				_localctx = new ComputedPropertyExpressionAssignmentContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1372);
				match(OpenBracket);
				setState(1373);
				singleExpression(0);
				setState(1374);
				match(CloseBracket);
				setState(1375);
				match(Colon);
				setState(1376);
				singleExpression(0);
				}
				break;
			case 3:
				_localctx = new PropertyGetterContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(1378);
				getAccessor();
				}
				break;
			case 4:
				_localctx = new PropertySetterContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(1379);
				setAccessor();
				}
				break;
			case 5:
				_localctx = new MethodPropertyContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(1380);
				generatorMethod();
				}
				break;
			case 6:
				_localctx = new PropertyShorthandContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(1381);
				identifierOrKeyWord();
				}
				break;
			case 7:
				_localctx = new RestParameterInObjectContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(1382);
				restParameter();
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
	public static class GetAccessorContext extends ParserRuleContext {
		public GetterContext getter() {
			return getRuleContext(GetterContext.class,0);
		}
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public GetAccessorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_getAccessor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterGetAccessor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitGetAccessor(this);
		}
	}

	public final GetAccessorContext getAccessor() throws RecognitionException {
		GetAccessorContext _localctx = new GetAccessorContext(_ctx, getState());
		enterRule(_localctx, 238, RULE_getAccessor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1385);
			getter();
			setState(1386);
			match(OpenParen);
			setState(1387);
			match(CloseParen);
			setState(1389);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Colon) {
				{
				setState(1388);
				typeAnnotation();
				}
			}

			setState(1391);
			match(OpenBrace);
			setState(1392);
			functionBody();
			setState(1393);
			match(CloseBrace);
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
	public static class SetAccessorContext extends ParserRuleContext {
		public SetterContext setter() {
			return getRuleContext(SetterContext.class,0);
		}
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public BindingPatternContext bindingPattern() {
			return getRuleContext(BindingPatternContext.class,0);
		}
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public SetAccessorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setAccessor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterSetAccessor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitSetAccessor(this);
		}
	}

	public final SetAccessorContext setAccessor() throws RecognitionException {
		SetAccessorContext _localctx = new SetAccessorContext(_ctx, getState());
		enterRule(_localctx, 240, RULE_setAccessor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1395);
			setter();
			setState(1396);
			match(OpenParen);
			setState(1399);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				{
				setState(1397);
				match(Identifier);
				}
				break;
			case OpenBracket:
			case OpenBrace:
				{
				setState(1398);
				bindingPattern();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(1402);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Colon) {
				{
				setState(1401);
				typeAnnotation();
				}
			}

			setState(1404);
			match(CloseParen);
			setState(1405);
			match(OpenBrace);
			setState(1406);
			functionBody();
			setState(1407);
			match(CloseBrace);
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
	public static class PropertyNameContext extends ParserRuleContext {
		public IdentifierNameContext identifierName() {
			return getRuleContext(IdentifierNameContext.class,0);
		}
		public TerminalNode StringLiteral() { return getToken(TypeScriptParser.StringLiteral, 0); }
		public NumericLiteralContext numericLiteral() {
			return getRuleContext(NumericLiteralContext.class,0);
		}
		public PropertyNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_propertyName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterPropertyName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitPropertyName(this);
		}
	}

	public final PropertyNameContext propertyName() throws RecognitionException {
		PropertyNameContext _localctx = new PropertyNameContext(_ctx, getState());
		enterRule(_localctx, 242, RULE_propertyName);
		try {
			setState(1412);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NullLiteral:
			case BooleanLiteral:
			case Break:
			case Do:
			case Instanceof:
			case Typeof:
			case Case:
			case Else:
			case New:
			case Var:
			case Catch:
			case Finally:
			case Return:
			case Void:
			case Continue:
			case For:
			case Switch:
			case While:
			case Debugger:
			case Function_:
			case This:
			case With:
			case Default:
			case If:
			case Throw:
			case Delete:
			case In:
			case Try:
			case From:
			case ReadOnly:
			case Async:
			case Class:
			case Enum:
			case Extends:
			case Super:
			case Const:
			case Export:
			case Import:
			case Implements:
			case Let:
			case Private:
			case Public:
			case Interface:
			case Package:
			case Protected:
			case Static:
			case Yield:
			case Number:
			case Boolean:
			case String:
			case TypeAlias:
			case Get:
			case Set:
			case Require:
			case Module:
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1409);
				identifierName();
				}
				break;
			case StringLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(1410);
				match(StringLiteral);
				}
				break;
			case DecimalLiteral:
			case HexIntegerLiteral:
			case OctalIntegerLiteral:
			case OctalIntegerLiteral2:
			case BinaryIntegerLiteral:
				enterOuterAlt(_localctx, 3);
				{
				setState(1411);
				numericLiteral();
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
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public TerminalNode Comma() { return getToken(TypeScriptParser.Comma, 0); }
		public ArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitArguments(this);
		}
	}

	public final ArgumentsContext arguments() throws RecognitionException {
		ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
		enterRule(_localctx, 244, RULE_arguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1414);
			match(OpenParen);
			setState(1419);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -36028795928641192L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 8128698260224212991L) != 0)) {
				{
				setState(1415);
				argumentList();
				setState(1417);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(1416);
					match(Comma);
					}
				}

				}
			}

			setState(1421);
			match(CloseParen);
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
	public static class ArgumentListContext extends ParserRuleContext {
		public List<ArgumentContext> argument() {
			return getRuleContexts(ArgumentContext.class);
		}
		public ArgumentContext argument(int i) {
			return getRuleContext(ArgumentContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public ArgumentListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterArgumentList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitArgumentList(this);
		}
	}

	public final ArgumentListContext argumentList() throws RecognitionException {
		ArgumentListContext _localctx = new ArgumentListContext(_ctx, getState());
		enterRule(_localctx, 246, RULE_argumentList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1423);
			argument();
			setState(1428);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,184,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1424);
					match(Comma);
					setState(1425);
					argument();
					}
					} 
				}
				setState(1430);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,184,_ctx);
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
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public TerminalNode Ellipsis() { return getToken(TypeScriptParser.Ellipsis, 0); }
		public ArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitArgument(this);
		}
	}

	public final ArgumentContext argument() throws RecognitionException {
		ArgumentContext _localctx = new ArgumentContext(_ctx, getState());
		enterRule(_localctx, 248, RULE_argument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1432);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Ellipsis) {
				{
				setState(1431);
				match(Ellipsis);
				}
			}

			setState(1436);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,186,_ctx) ) {
			case 1:
				{
				setState(1434);
				singleExpression(0);
				}
				break;
			case 2:
				{
				setState(1435);
				match(Identifier);
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
	public static class ExpressionSequenceContext extends ParserRuleContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(TypeScriptParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(TypeScriptParser.Comma, i);
		}
		public ExpressionSequenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionSequence; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterExpressionSequence(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitExpressionSequence(this);
		}
	}

	public final ExpressionSequenceContext expressionSequence() throws RecognitionException {
		ExpressionSequenceContext _localctx = new ExpressionSequenceContext(_ctx, getState());
		enterRule(_localctx, 250, RULE_expressionSequence);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1438);
			singleExpression(0);
			setState(1443);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,187,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1439);
					match(Comma);
					setState(1440);
					singleExpression(0);
					}
					} 
				}
				setState(1445);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,187,_ctx);
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
	public static class FunctionExpressionDeclarationContext extends ParserRuleContext {
		public TerminalNode Function_() { return getToken(TypeScriptParser.Function_, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public FormalParameterListContext formalParameterList() {
			return getRuleContext(FormalParameterListContext.class,0);
		}
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public FunctionExpressionDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionExpressionDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterFunctionExpressionDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitFunctionExpressionDeclaration(this);
		}
	}

	public final FunctionExpressionDeclarationContext functionExpressionDeclaration() throws RecognitionException {
		FunctionExpressionDeclarationContext _localctx = new FunctionExpressionDeclarationContext(_ctx, getState());
		enterRule(_localctx, 252, RULE_functionExpressionDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1446);
			match(Function_);
			setState(1448);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(1447);
				match(Identifier);
				}
			}

			setState(1450);
			match(OpenParen);
			setState(1452);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 65808L) != 0) || ((((_la - 101)) & ~0x3f) == 0 && ((1L << (_la - 101)) & 12718099L) != 0)) {
				{
				setState(1451);
				formalParameterList();
				}
			}

			setState(1454);
			match(CloseParen);
			setState(1456);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Colon) {
				{
				setState(1455);
				typeAnnotation();
				}
			}

			setState(1458);
			match(OpenBrace);
			setState(1459);
			functionBody();
			setState(1460);
			match(CloseBrace);
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
	public static class SingleExpressionContext extends ParserRuleContext {
		public SingleExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleExpression; }
	 
		public SingleExpressionContext() { }
		public void copyFrom(SingleExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TemplateStringExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TemplateStringLiteralContext templateStringLiteral() {
			return getRuleContext(TemplateStringLiteralContext.class,0);
		}
		public TemplateStringExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTemplateStringExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTemplateStringExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TernaryExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode QuestionMark() { return getToken(TypeScriptParser.QuestionMark, 0); }
		public TerminalNode Colon() { return getToken(TypeScriptParser.Colon, 0); }
		public TernaryExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTernaryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTernaryExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LogicalAndExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode And() { return getToken(TypeScriptParser.And, 0); }
		public LogicalAndExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterLogicalAndExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitLogicalAndExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class GeneratorsExpressionContext extends SingleExpressionContext {
		public GeneratorBlockContext generatorBlock() {
			return getRuleContext(GeneratorBlockContext.class,0);
		}
		public GeneratorsExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterGeneratorsExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitGeneratorsExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PreIncrementExpressionContext extends SingleExpressionContext {
		public TerminalNode PlusPlus() { return getToken(TypeScriptParser.PlusPlus, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public PreIncrementExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterPreIncrementExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitPreIncrementExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ObjectLiteralExpressionContext extends SingleExpressionContext {
		public ObjectLiteralContext objectLiteral() {
			return getRuleContext(ObjectLiteralContext.class,0);
		}
		public ObjectLiteralExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterObjectLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitObjectLiteralExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class InExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode In() { return getToken(TypeScriptParser.In, 0); }
		public InExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterInExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitInExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LogicalOrExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode Or() { return getToken(TypeScriptParser.Or, 0); }
		public LogicalOrExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterLogicalOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitLogicalOrExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class GenericTypesContext extends SingleExpressionContext {
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public GenericTypesContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterGenericTypes(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitGenericTypes(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NotExpressionContext extends SingleExpressionContext {
		public TerminalNode Not() { return getToken(TypeScriptParser.Not, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public NotExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterNotExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitNotExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PreDecreaseExpressionContext extends SingleExpressionContext {
		public TerminalNode MinusMinus() { return getToken(TypeScriptParser.MinusMinus, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public PreDecreaseExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterPreDecreaseExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitPreDecreaseExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentsExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public ArgumentsExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterArgumentsExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitArgumentsExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ThisExpressionContext extends SingleExpressionContext {
		public TerminalNode This() { return getToken(TypeScriptParser.This, 0); }
		public ThisExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterThisExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitThisExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunctionExpressionContext extends SingleExpressionContext {
		public FunctionExpressionDeclarationContext functionExpressionDeclaration() {
			return getRuleContext(FunctionExpressionDeclarationContext.class,0);
		}
		public FunctionExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterFunctionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitFunctionExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class UnaryMinusExpressionContext extends SingleExpressionContext {
		public TerminalNode Minus() { return getToken(TypeScriptParser.Minus, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public UnaryMinusExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterUnaryMinusExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitUnaryMinusExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AssignmentExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode Assign() { return getToken(TypeScriptParser.Assign, 0); }
		public AssignmentExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterAssignmentExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitAssignmentExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PostDecreaseExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TerminalNode MinusMinus() { return getToken(TypeScriptParser.MinusMinus, 0); }
		public PostDecreaseExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterPostDecreaseExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitPostDecreaseExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TypeofExpressionContext extends SingleExpressionContext {
		public TerminalNode Typeof() { return getToken(TypeScriptParser.Typeof, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TypeofExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTypeofExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTypeofExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class InstanceofExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode Instanceof() { return getToken(TypeScriptParser.Instanceof, 0); }
		public InstanceofExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterInstanceofExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitInstanceofExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class UnaryPlusExpressionContext extends SingleExpressionContext {
		public TerminalNode Plus() { return getToken(TypeScriptParser.Plus, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public UnaryPlusExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterUnaryPlusExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitUnaryPlusExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DeleteExpressionContext extends SingleExpressionContext {
		public TerminalNode Delete() { return getToken(TypeScriptParser.Delete, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public DeleteExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterDeleteExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitDeleteExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class GeneratorsFunctionExpressionContext extends SingleExpressionContext {
		public GeneratorFunctionDeclarationContext generatorFunctionDeclaration() {
			return getRuleContext(GeneratorFunctionDeclarationContext.class,0);
		}
		public GeneratorsFunctionExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterGeneratorsFunctionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitGeneratorsFunctionExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ArrowFunctionExpressionContext extends SingleExpressionContext {
		public ArrowFunctionDeclarationContext arrowFunctionDeclaration() {
			return getRuleContext(ArrowFunctionDeclarationContext.class,0);
		}
		public ArrowFunctionExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterArrowFunctionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitArrowFunctionExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IteratorsExpressionContext extends SingleExpressionContext {
		public IteratorBlockContext iteratorBlock() {
			return getRuleContext(IteratorBlockContext.class,0);
		}
		public IteratorsExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterIteratorsExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitIteratorsExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class EqualityExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode Equals_() { return getToken(TypeScriptParser.Equals_, 0); }
		public TerminalNode NotEquals() { return getToken(TypeScriptParser.NotEquals, 0); }
		public TerminalNode IdentityEquals() { return getToken(TypeScriptParser.IdentityEquals, 0); }
		public TerminalNode IdentityNotEquals() { return getToken(TypeScriptParser.IdentityNotEquals, 0); }
		public EqualityExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterEqualityExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitEqualityExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BitXOrExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode BitXOr() { return getToken(TypeScriptParser.BitXOr, 0); }
		public BitXOrExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterBitXOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitBitXOrExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class CastAsExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TerminalNode As() { return getToken(TypeScriptParser.As, 0); }
		public AsExpressionContext asExpression() {
			return getRuleContext(AsExpressionContext.class,0);
		}
		public CastAsExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterCastAsExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitCastAsExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SuperExpressionContext extends SingleExpressionContext {
		public TerminalNode Super() { return getToken(TypeScriptParser.Super, 0); }
		public SuperExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterSuperExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitSuperExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MultiplicativeExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode Multiply() { return getToken(TypeScriptParser.Multiply, 0); }
		public TerminalNode Divide() { return getToken(TypeScriptParser.Divide, 0); }
		public TerminalNode Modulus() { return getToken(TypeScriptParser.Modulus, 0); }
		public MultiplicativeExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterMultiplicativeExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitMultiplicativeExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BitShiftExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode LeftShiftArithmetic() { return getToken(TypeScriptParser.LeftShiftArithmetic, 0); }
		public TerminalNode RightShiftArithmetic() { return getToken(TypeScriptParser.RightShiftArithmetic, 0); }
		public TerminalNode RightShiftLogical() { return getToken(TypeScriptParser.RightShiftLogical, 0); }
		public BitShiftExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterBitShiftExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitBitShiftExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParenthesizedExpressionContext extends SingleExpressionContext {
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public ParenthesizedExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterParenthesizedExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitParenthesizedExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AdditiveExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode Plus() { return getToken(TypeScriptParser.Plus, 0); }
		public TerminalNode Minus() { return getToken(TypeScriptParser.Minus, 0); }
		public AdditiveExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterAdditiveExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitAdditiveExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RelationalExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode LessThan() { return getToken(TypeScriptParser.LessThan, 0); }
		public TerminalNode MoreThan() { return getToken(TypeScriptParser.MoreThan, 0); }
		public TerminalNode LessThanEquals() { return getToken(TypeScriptParser.LessThanEquals, 0); }
		public TerminalNode GreaterThanEquals() { return getToken(TypeScriptParser.GreaterThanEquals, 0); }
		public RelationalExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterRelationalExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitRelationalExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PostIncrementExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TerminalNode PlusPlus() { return getToken(TypeScriptParser.PlusPlus, 0); }
		public PostIncrementExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterPostIncrementExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitPostIncrementExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class YieldExpressionContext extends SingleExpressionContext {
		public YieldStatementContext yieldStatement() {
			return getRuleContext(YieldStatementContext.class,0);
		}
		public YieldExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterYieldExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitYieldExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BitNotExpressionContext extends SingleExpressionContext {
		public TerminalNode BitNot() { return getToken(TypeScriptParser.BitNot, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public BitNotExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterBitNotExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitBitNotExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NewExpressionContext extends SingleExpressionContext {
		public TerminalNode New() { return getToken(TypeScriptParser.New, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public NewExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterNewExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitNewExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LiteralExpressionContext extends SingleExpressionContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public LiteralExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitLiteralExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ArrayLiteralExpressionContext extends SingleExpressionContext {
		public ArrayLiteralContext arrayLiteral() {
			return getRuleContext(ArrayLiteralContext.class,0);
		}
		public ArrayLiteralExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterArrayLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitArrayLiteralExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MemberDotExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TerminalNode Dot() { return getToken(TypeScriptParser.Dot, 0); }
		public IdentifierNameContext identifierName() {
			return getRuleContext(IdentifierNameContext.class,0);
		}
		public TerminalNode Not() { return getToken(TypeScriptParser.Not, 0); }
		public NestedTypeGenericContext nestedTypeGeneric() {
			return getRuleContext(NestedTypeGenericContext.class,0);
		}
		public MemberDotExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterMemberDotExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitMemberDotExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MemberIndexExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TerminalNode OpenBracket() { return getToken(TypeScriptParser.OpenBracket, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public TerminalNode CloseBracket() { return getToken(TypeScriptParser.CloseBracket, 0); }
		public MemberIndexExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterMemberIndexExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitMemberIndexExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IdentifierExpressionContext extends SingleExpressionContext {
		public IdentifierNameContext identifierName() {
			return getRuleContext(IdentifierNameContext.class,0);
		}
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public IdentifierExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterIdentifierExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitIdentifierExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BitAndExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode BitAnd() { return getToken(TypeScriptParser.BitAnd, 0); }
		public BitAndExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterBitAndExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitBitAndExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BitOrExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode BitOr() { return getToken(TypeScriptParser.BitOr, 0); }
		public BitOrExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterBitOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitBitOrExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AssignmentOperatorExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public AssignmentOperatorContext assignmentOperator() {
			return getRuleContext(AssignmentOperatorContext.class,0);
		}
		public AssignmentOperatorExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterAssignmentOperatorExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitAssignmentOperatorExpression(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class VoidExpressionContext extends SingleExpressionContext {
		public TerminalNode Void() { return getToken(TypeScriptParser.Void, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public VoidExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterVoidExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitVoidExpression(this);
		}
	}

	public final SingleExpressionContext singleExpression() throws RecognitionException {
		return singleExpression(0);
	}

	private SingleExpressionContext singleExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		SingleExpressionContext _localctx = new SingleExpressionContext(_ctx, _parentState);
		SingleExpressionContext _prevctx = _localctx;
		int _startState = 254;
		enterRecursionRule(_localctx, 254, RULE_singleExpression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1516);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,195,_ctx) ) {
			case 1:
				{
				_localctx = new FunctionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(1463);
				functionExpressionDeclaration();
				}
				break;
			case 2:
				{
				_localctx = new ArrowFunctionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1464);
				arrowFunctionDeclaration();
				}
				break;
			case 3:
				{
				_localctx = new NewExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1465);
				match(New);
				setState(1466);
				singleExpression(0);
				setState(1468);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LessThan) {
					{
					setState(1467);
					typeArguments();
					}
				}

				setState(1470);
				arguments();
				}
				break;
			case 4:
				{
				_localctx = new NewExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1472);
				match(New);
				setState(1473);
				singleExpression(0);
				setState(1475);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,192,_ctx) ) {
				case 1:
					{
					setState(1474);
					typeArguments();
					}
					break;
				}
				}
				break;
			case 5:
				{
				_localctx = new DeleteExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1477);
				match(Delete);
				setState(1478);
				singleExpression(38);
				}
				break;
			case 6:
				{
				_localctx = new VoidExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1479);
				match(Void);
				setState(1480);
				singleExpression(37);
				}
				break;
			case 7:
				{
				_localctx = new TypeofExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1481);
				match(Typeof);
				setState(1482);
				singleExpression(36);
				}
				break;
			case 8:
				{
				_localctx = new PreIncrementExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1483);
				match(PlusPlus);
				setState(1484);
				singleExpression(35);
				}
				break;
			case 9:
				{
				_localctx = new PreDecreaseExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1485);
				match(MinusMinus);
				setState(1486);
				singleExpression(34);
				}
				break;
			case 10:
				{
				_localctx = new UnaryPlusExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1487);
				match(Plus);
				setState(1488);
				singleExpression(33);
				}
				break;
			case 11:
				{
				_localctx = new UnaryMinusExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1489);
				match(Minus);
				setState(1490);
				singleExpression(32);
				}
				break;
			case 12:
				{
				_localctx = new BitNotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1491);
				match(BitNot);
				setState(1492);
				singleExpression(31);
				}
				break;
			case 13:
				{
				_localctx = new NotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1493);
				match(Not);
				setState(1494);
				singleExpression(30);
				}
				break;
			case 14:
				{
				_localctx = new IteratorsExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1495);
				iteratorBlock();
				}
				break;
			case 15:
				{
				_localctx = new GeneratorsExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1496);
				generatorBlock();
				}
				break;
			case 16:
				{
				_localctx = new GeneratorsFunctionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1497);
				generatorFunctionDeclaration();
				}
				break;
			case 17:
				{
				_localctx = new YieldExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1498);
				yieldStatement();
				}
				break;
			case 18:
				{
				_localctx = new ThisExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1499);
				match(This);
				}
				break;
			case 19:
				{
				_localctx = new IdentifierExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1500);
				identifierName();
				setState(1502);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,193,_ctx) ) {
				case 1:
					{
					setState(1501);
					singleExpression(0);
					}
					break;
				}
				}
				break;
			case 20:
				{
				_localctx = new SuperExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1504);
				match(Super);
				}
				break;
			case 21:
				{
				_localctx = new LiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1505);
				literal();
				}
				break;
			case 22:
				{
				_localctx = new ArrayLiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1506);
				arrayLiteral();
				}
				break;
			case 23:
				{
				_localctx = new ObjectLiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1507);
				objectLiteral();
				}
				break;
			case 24:
				{
				_localctx = new ParenthesizedExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1508);
				match(OpenParen);
				setState(1509);
				expressionSequence();
				setState(1510);
				match(CloseParen);
				}
				break;
			case 25:
				{
				_localctx = new GenericTypesContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1512);
				typeArguments();
				setState(1514);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,194,_ctx) ) {
				case 1:
					{
					setState(1513);
					expressionSequence();
					}
					break;
				}
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(1596);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,199,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(1594);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,198,_ctx) ) {
					case 1:
						{
						_localctx = new MultiplicativeExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1518);
						if (!(precpred(_ctx, 29))) throw new FailedPredicateException(this, "precpred(_ctx, 29)");
						setState(1519);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 117440512L) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1520);
						singleExpression(30);
						}
						break;
					case 2:
						{
						_localctx = new AdditiveExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1521);
						if (!(precpred(_ctx, 28))) throw new FailedPredicateException(this, "precpred(_ctx, 28)");
						setState(1522);
						_la = _input.LA(1);
						if ( !(_la==Plus || _la==Minus) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1523);
						singleExpression(29);
						}
						break;
					case 3:
						{
						_localctx = new BitShiftExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1524);
						if (!(precpred(_ctx, 27))) throw new FailedPredicateException(this, "precpred(_ctx, 27)");
						setState(1525);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 939524096L) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1526);
						singleExpression(28);
						}
						break;
					case 4:
						{
						_localctx = new RelationalExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1527);
						if (!(precpred(_ctx, 26))) throw new FailedPredicateException(this, "precpred(_ctx, 26)");
						setState(1528);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 16106127360L) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1529);
						singleExpression(27);
						}
						break;
					case 5:
						{
						_localctx = new InstanceofExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1530);
						if (!(precpred(_ctx, 25))) throw new FailedPredicateException(this, "precpred(_ctx, 25)");
						setState(1531);
						match(Instanceof);
						setState(1532);
						singleExpression(26);
						}
						break;
					case 6:
						{
						_localctx = new InExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1533);
						if (!(precpred(_ctx, 24))) throw new FailedPredicateException(this, "precpred(_ctx, 24)");
						setState(1534);
						match(In);
						setState(1535);
						singleExpression(25);
						}
						break;
					case 7:
						{
						_localctx = new EqualityExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1536);
						if (!(precpred(_ctx, 23))) throw new FailedPredicateException(this, "precpred(_ctx, 23)");
						setState(1537);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 257698037760L) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1538);
						singleExpression(24);
						}
						break;
					case 8:
						{
						_localctx = new BitAndExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1539);
						if (!(precpred(_ctx, 22))) throw new FailedPredicateException(this, "precpred(_ctx, 22)");
						setState(1540);
						match(BitAnd);
						setState(1541);
						singleExpression(23);
						}
						break;
					case 9:
						{
						_localctx = new BitXOrExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1542);
						if (!(precpred(_ctx, 21))) throw new FailedPredicateException(this, "precpred(_ctx, 21)");
						setState(1543);
						match(BitXOr);
						setState(1544);
						singleExpression(22);
						}
						break;
					case 10:
						{
						_localctx = new BitOrExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1545);
						if (!(precpred(_ctx, 20))) throw new FailedPredicateException(this, "precpred(_ctx, 20)");
						setState(1546);
						match(BitOr);
						setState(1547);
						singleExpression(21);
						}
						break;
					case 11:
						{
						_localctx = new LogicalAndExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1548);
						if (!(precpred(_ctx, 19))) throw new FailedPredicateException(this, "precpred(_ctx, 19)");
						setState(1549);
						match(And);
						setState(1550);
						singleExpression(20);
						}
						break;
					case 12:
						{
						_localctx = new LogicalOrExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1551);
						if (!(precpred(_ctx, 18))) throw new FailedPredicateException(this, "precpred(_ctx, 18)");
						setState(1552);
						match(Or);
						setState(1553);
						singleExpression(19);
						}
						break;
					case 13:
						{
						_localctx = new TernaryExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1554);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(1555);
						match(QuestionMark);
						setState(1556);
						singleExpression(0);
						setState(1557);
						match(Colon);
						setState(1558);
						singleExpression(18);
						}
						break;
					case 14:
						{
						_localctx = new AssignmentExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1560);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(1561);
						match(Assign);
						setState(1562);
						singleExpression(17);
						}
						break;
					case 15:
						{
						_localctx = new AssignmentOperatorExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1563);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(1564);
						assignmentOperator();
						setState(1565);
						singleExpression(16);
						}
						break;
					case 16:
						{
						_localctx = new MemberIndexExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1567);
						if (!(precpred(_ctx, 45))) throw new FailedPredicateException(this, "precpred(_ctx, 45)");
						setState(1568);
						match(OpenBracket);
						setState(1569);
						expressionSequence();
						setState(1570);
						match(CloseBracket);
						}
						break;
					case 17:
						{
						_localctx = new MemberDotExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1572);
						if (!(precpred(_ctx, 44))) throw new FailedPredicateException(this, "precpred(_ctx, 44)");
						setState(1574);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(1573);
							match(Not);
							}
						}

						setState(1576);
						match(Dot);
						setState(1577);
						identifierName();
						setState(1579);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,197,_ctx) ) {
						case 1:
							{
							setState(1578);
							nestedTypeGeneric();
							}
							break;
						}
						}
						break;
					case 18:
						{
						_localctx = new ArgumentsExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1581);
						if (!(precpred(_ctx, 41))) throw new FailedPredicateException(this, "precpred(_ctx, 41)");
						setState(1582);
						arguments();
						}
						break;
					case 19:
						{
						_localctx = new PostIncrementExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1583);
						if (!(precpred(_ctx, 40))) throw new FailedPredicateException(this, "precpred(_ctx, 40)");
						setState(1584);
						if (!(this.notLineTerminator())) throw new FailedPredicateException(this, "this.notLineTerminator()");
						setState(1585);
						match(PlusPlus);
						}
						break;
					case 20:
						{
						_localctx = new PostDecreaseExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1586);
						if (!(precpred(_ctx, 39))) throw new FailedPredicateException(this, "precpred(_ctx, 39)");
						setState(1587);
						if (!(this.notLineTerminator())) throw new FailedPredicateException(this, "this.notLineTerminator()");
						setState(1588);
						match(MinusMinus);
						}
						break;
					case 21:
						{
						_localctx = new TemplateStringExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1589);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(1590);
						templateStringLiteral();
						}
						break;
					case 22:
						{
						_localctx = new CastAsExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1591);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(1592);
						match(As);
						setState(1593);
						asExpression();
						}
						break;
					}
					} 
				}
				setState(1598);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,199,_ctx);
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
	public static class AsExpressionContext extends ParserRuleContext {
		public PredefinedTypeContext predefinedType() {
			return getRuleContext(PredefinedTypeContext.class,0);
		}
		public TerminalNode OpenBracket() { return getToken(TypeScriptParser.OpenBracket, 0); }
		public TerminalNode CloseBracket() { return getToken(TypeScriptParser.CloseBracket, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public AsExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_asExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterAsExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitAsExpression(this);
		}
	}

	public final AsExpressionContext asExpression() throws RecognitionException {
		AsExpressionContext _localctx = new AsExpressionContext(_ctx, getState());
		enterRule(_localctx, 256, RULE_asExpression);
		try {
			setState(1605);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,201,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1599);
				predefinedType();
				setState(1602);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,200,_ctx) ) {
				case 1:
					{
					setState(1600);
					match(OpenBracket);
					setState(1601);
					match(CloseBracket);
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1604);
				singleExpression(0);
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
	public static class ArrowFunctionDeclarationContext extends ParserRuleContext {
		public ArrowFunctionParametersContext arrowFunctionParameters() {
			return getRuleContext(ArrowFunctionParametersContext.class,0);
		}
		public TerminalNode ARROW() { return getToken(TypeScriptParser.ARROW, 0); }
		public ArrowFunctionBodyContext arrowFunctionBody() {
			return getRuleContext(ArrowFunctionBodyContext.class,0);
		}
		public TerminalNode Async() { return getToken(TypeScriptParser.Async, 0); }
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public ArrowFunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrowFunctionDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterArrowFunctionDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitArrowFunctionDeclaration(this);
		}
	}

	public final ArrowFunctionDeclarationContext arrowFunctionDeclaration() throws RecognitionException {
		ArrowFunctionDeclarationContext _localctx = new ArrowFunctionDeclarationContext(_ctx, getState());
		enterRule(_localctx, 258, RULE_arrowFunctionDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1608);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Async) {
				{
				setState(1607);
				match(Async);
				}
			}

			setState(1610);
			arrowFunctionParameters();
			setState(1612);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Colon) {
				{
				setState(1611);
				typeAnnotation();
				}
			}

			setState(1614);
			match(ARROW);
			setState(1615);
			arrowFunctionBody();
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
	public static class ArrowFunctionParametersContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public TerminalNode OpenParen() { return getToken(TypeScriptParser.OpenParen, 0); }
		public TerminalNode CloseParen() { return getToken(TypeScriptParser.CloseParen, 0); }
		public FormalParameterListContext formalParameterList() {
			return getRuleContext(FormalParameterListContext.class,0);
		}
		public ArrowFunctionParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrowFunctionParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterArrowFunctionParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitArrowFunctionParameters(this);
		}
	}

	public final ArrowFunctionParametersContext arrowFunctionParameters() throws RecognitionException {
		ArrowFunctionParametersContext _localctx = new ArrowFunctionParametersContext(_ctx, getState());
		enterRule(_localctx, 260, RULE_arrowFunctionParameters);
		int _la;
		try {
			setState(1623);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1617);
				match(Identifier);
				}
				break;
			case OpenParen:
				enterOuterAlt(_localctx, 2);
				{
				setState(1618);
				match(OpenParen);
				setState(1620);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 65808L) != 0) || ((((_la - 101)) & ~0x3f) == 0 && ((1L << (_la - 101)) & 12718099L) != 0)) {
					{
					setState(1619);
					formalParameterList();
					}
				}

				setState(1622);
				match(CloseParen);
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
	public static class ArrowFunctionBodyContext extends ParserRuleContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TerminalNode OpenBrace() { return getToken(TypeScriptParser.OpenBrace, 0); }
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public TerminalNode CloseBrace() { return getToken(TypeScriptParser.CloseBrace, 0); }
		public ArrowFunctionBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrowFunctionBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterArrowFunctionBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitArrowFunctionBody(this);
		}
	}

	public final ArrowFunctionBodyContext arrowFunctionBody() throws RecognitionException {
		ArrowFunctionBodyContext _localctx = new ArrowFunctionBodyContext(_ctx, getState());
		enterRule(_localctx, 262, RULE_arrowFunctionBody);
		try {
			setState(1630);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,206,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1625);
				singleExpression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1626);
				match(OpenBrace);
				setState(1627);
				functionBody();
				setState(1628);
				match(CloseBrace);
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
	public static class AssignmentOperatorContext extends ParserRuleContext {
		public TerminalNode MultiplyAssign() { return getToken(TypeScriptParser.MultiplyAssign, 0); }
		public TerminalNode DivideAssign() { return getToken(TypeScriptParser.DivideAssign, 0); }
		public TerminalNode ModulusAssign() { return getToken(TypeScriptParser.ModulusAssign, 0); }
		public TerminalNode PlusAssign() { return getToken(TypeScriptParser.PlusAssign, 0); }
		public TerminalNode MinusAssign() { return getToken(TypeScriptParser.MinusAssign, 0); }
		public TerminalNode LeftShiftArithmeticAssign() { return getToken(TypeScriptParser.LeftShiftArithmeticAssign, 0); }
		public TerminalNode RightShiftArithmeticAssign() { return getToken(TypeScriptParser.RightShiftArithmeticAssign, 0); }
		public TerminalNode RightShiftLogicalAssign() { return getToken(TypeScriptParser.RightShiftLogicalAssign, 0); }
		public TerminalNode BitAndAssign() { return getToken(TypeScriptParser.BitAndAssign, 0); }
		public TerminalNode BitXorAssign() { return getToken(TypeScriptParser.BitXorAssign, 0); }
		public TerminalNode BitOrAssign() { return getToken(TypeScriptParser.BitOrAssign, 0); }
		public AssignmentOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignmentOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterAssignmentOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitAssignmentOperator(this);
		}
	}

	public final AssignmentOperatorContext assignmentOperator() throws RecognitionException {
		AssignmentOperatorContext _localctx = new AssignmentOperatorContext(_ctx, getState());
		enterRule(_localctx, 264, RULE_assignmentOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1632);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 18005602416459776L) != 0)) ) {
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
	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode NullLiteral() { return getToken(TypeScriptParser.NullLiteral, 0); }
		public TerminalNode BooleanLiteral() { return getToken(TypeScriptParser.BooleanLiteral, 0); }
		public TerminalNode StringLiteral() { return getToken(TypeScriptParser.StringLiteral, 0); }
		public TemplateStringLiteralContext templateStringLiteral() {
			return getRuleContext(TemplateStringLiteralContext.class,0);
		}
		public TerminalNode RegularExpressionLiteral() { return getToken(TypeScriptParser.RegularExpressionLiteral, 0); }
		public NumericLiteralContext numericLiteral() {
			return getRuleContext(NumericLiteralContext.class,0);
		}
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitLiteral(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 266, RULE_literal);
		try {
			setState(1640);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NullLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(1634);
				match(NullLiteral);
				}
				break;
			case BooleanLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(1635);
				match(BooleanLiteral);
				}
				break;
			case StringLiteral:
				enterOuterAlt(_localctx, 3);
				{
				setState(1636);
				match(StringLiteral);
				}
				break;
			case BackTick:
				enterOuterAlt(_localctx, 4);
				{
				setState(1637);
				templateStringLiteral();
				}
				break;
			case RegularExpressionLiteral:
				enterOuterAlt(_localctx, 5);
				{
				setState(1638);
				match(RegularExpressionLiteral);
				}
				break;
			case DecimalLiteral:
			case HexIntegerLiteral:
			case OctalIntegerLiteral:
			case OctalIntegerLiteral2:
			case BinaryIntegerLiteral:
				enterOuterAlt(_localctx, 6);
				{
				setState(1639);
				numericLiteral();
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
	public static class TemplateStringLiteralContext extends ParserRuleContext {
		public List<TerminalNode> BackTick() { return getTokens(TypeScriptParser.BackTick); }
		public TerminalNode BackTick(int i) {
			return getToken(TypeScriptParser.BackTick, i);
		}
		public List<TemplateStringAtomContext> templateStringAtom() {
			return getRuleContexts(TemplateStringAtomContext.class);
		}
		public TemplateStringAtomContext templateStringAtom(int i) {
			return getRuleContext(TemplateStringAtomContext.class,i);
		}
		public TemplateStringLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_templateStringLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTemplateStringLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTemplateStringLiteral(this);
		}
	}

	public final TemplateStringLiteralContext templateStringLiteral() throws RecognitionException {
		TemplateStringLiteralContext _localctx = new TemplateStringLiteralContext(_ctx, getState());
		enterRule(_localctx, 268, RULE_templateStringLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1642);
			match(BackTick);
			setState(1646);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 132)) & ~0x3f) == 0 && ((1L << (_la - 132)) & 7L) != 0)) {
				{
				{
				setState(1643);
				templateStringAtom();
				}
				}
				setState(1648);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1649);
			match(BackTick);
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
	public static class TemplateStringAtomContext extends ParserRuleContext {
		public TerminalNode TemplateStringAtom() { return getToken(TypeScriptParser.TemplateStringAtom, 0); }
		public TerminalNode TemplateStringStartExpression() { return getToken(TypeScriptParser.TemplateStringStartExpression, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TerminalNode TemplateCloseBrace() { return getToken(TypeScriptParser.TemplateCloseBrace, 0); }
		public TerminalNode TemplateStringEscapeAtom() { return getToken(TypeScriptParser.TemplateStringEscapeAtom, 0); }
		public TemplateStringAtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_templateStringAtom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterTemplateStringAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitTemplateStringAtom(this);
		}
	}

	public final TemplateStringAtomContext templateStringAtom() throws RecognitionException {
		TemplateStringAtomContext _localctx = new TemplateStringAtomContext(_ctx, getState());
		enterRule(_localctx, 270, RULE_templateStringAtom);
		try {
			setState(1657);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TemplateStringAtom:
				enterOuterAlt(_localctx, 1);
				{
				setState(1651);
				match(TemplateStringAtom);
				}
				break;
			case TemplateStringStartExpression:
				enterOuterAlt(_localctx, 2);
				{
				setState(1652);
				match(TemplateStringStartExpression);
				setState(1653);
				singleExpression(0);
				setState(1654);
				match(TemplateCloseBrace);
				}
				break;
			case TemplateStringEscapeAtom:
				enterOuterAlt(_localctx, 3);
				{
				setState(1656);
				match(TemplateStringEscapeAtom);
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
	public static class NumericLiteralContext extends ParserRuleContext {
		public TerminalNode DecimalLiteral() { return getToken(TypeScriptParser.DecimalLiteral, 0); }
		public TerminalNode HexIntegerLiteral() { return getToken(TypeScriptParser.HexIntegerLiteral, 0); }
		public TerminalNode OctalIntegerLiteral() { return getToken(TypeScriptParser.OctalIntegerLiteral, 0); }
		public TerminalNode OctalIntegerLiteral2() { return getToken(TypeScriptParser.OctalIntegerLiteral2, 0); }
		public TerminalNode BinaryIntegerLiteral() { return getToken(TypeScriptParser.BinaryIntegerLiteral, 0); }
		public NumericLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numericLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterNumericLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitNumericLiteral(this);
		}
	}

	public final NumericLiteralContext numericLiteral() throws RecognitionException {
		NumericLiteralContext _localctx = new NumericLiteralContext(_ctx, getState());
		enterRule(_localctx, 272, RULE_numericLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1659);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 4467570830351532032L) != 0)) ) {
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
	public static class IdentifierNameContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public ReservedWordContext reservedWord() {
			return getRuleContext(ReservedWordContext.class,0);
		}
		public IdentifierNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifierName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterIdentifierName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitIdentifierName(this);
		}
	}

	public final IdentifierNameContext identifierName() throws RecognitionException {
		IdentifierNameContext _localctx = new IdentifierNameContext(_ctx, getState());
		enterRule(_localctx, 274, RULE_identifierName);
		try {
			setState(1663);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1661);
				match(Identifier);
				}
				break;
			case NullLiteral:
			case BooleanLiteral:
			case Break:
			case Do:
			case Instanceof:
			case Typeof:
			case Case:
			case Else:
			case New:
			case Var:
			case Catch:
			case Finally:
			case Return:
			case Void:
			case Continue:
			case For:
			case Switch:
			case While:
			case Debugger:
			case Function_:
			case This:
			case With:
			case Default:
			case If:
			case Throw:
			case Delete:
			case In:
			case Try:
			case From:
			case ReadOnly:
			case Async:
			case Class:
			case Enum:
			case Extends:
			case Super:
			case Const:
			case Export:
			case Import:
			case Implements:
			case Let:
			case Private:
			case Public:
			case Interface:
			case Package:
			case Protected:
			case Static:
			case Yield:
			case Number:
			case Boolean:
			case String:
			case TypeAlias:
			case Get:
			case Set:
			case Require:
			case Module:
				enterOuterAlt(_localctx, 2);
				{
				setState(1662);
				reservedWord();
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
	public static class IdentifierOrKeyWordContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(TypeScriptParser.Identifier, 0); }
		public TerminalNode TypeAlias() { return getToken(TypeScriptParser.TypeAlias, 0); }
		public TerminalNode Require() { return getToken(TypeScriptParser.Require, 0); }
		public IdentifierOrKeyWordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifierOrKeyWord; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterIdentifierOrKeyWord(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitIdentifierOrKeyWord(this);
		}
	}

	public final IdentifierOrKeyWordContext identifierOrKeyWord() throws RecognitionException {
		IdentifierOrKeyWordContext _localctx = new IdentifierOrKeyWordContext(_ctx, getState());
		enterRule(_localctx, 276, RULE_identifierOrKeyWord);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1665);
			_la = _input.LA(1);
			if ( !(((((_la - 113)) & ~0x3f) == 0 && ((1L << (_la - 113)) & 2081L) != 0)) ) {
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
	public static class ReservedWordContext extends ParserRuleContext {
		public KeywordContext keyword() {
			return getRuleContext(KeywordContext.class,0);
		}
		public TerminalNode NullLiteral() { return getToken(TypeScriptParser.NullLiteral, 0); }
		public TerminalNode BooleanLiteral() { return getToken(TypeScriptParser.BooleanLiteral, 0); }
		public ReservedWordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_reservedWord; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterReservedWord(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitReservedWord(this);
		}
	}

	public final ReservedWordContext reservedWord() throws RecognitionException {
		ReservedWordContext _localctx = new ReservedWordContext(_ctx, getState());
		enterRule(_localctx, 278, RULE_reservedWord);
		try {
			setState(1670);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Break:
			case Do:
			case Instanceof:
			case Typeof:
			case Case:
			case Else:
			case New:
			case Var:
			case Catch:
			case Finally:
			case Return:
			case Void:
			case Continue:
			case For:
			case Switch:
			case While:
			case Debugger:
			case Function_:
			case This:
			case With:
			case Default:
			case If:
			case Throw:
			case Delete:
			case In:
			case Try:
			case From:
			case ReadOnly:
			case Async:
			case Class:
			case Enum:
			case Extends:
			case Super:
			case Const:
			case Export:
			case Import:
			case Implements:
			case Let:
			case Private:
			case Public:
			case Interface:
			case Package:
			case Protected:
			case Static:
			case Yield:
			case Number:
			case Boolean:
			case String:
			case TypeAlias:
			case Get:
			case Set:
			case Require:
			case Module:
				enterOuterAlt(_localctx, 1);
				{
				setState(1667);
				keyword();
				}
				break;
			case NullLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(1668);
				match(NullLiteral);
				}
				break;
			case BooleanLiteral:
				enterOuterAlt(_localctx, 3);
				{
				setState(1669);
				match(BooleanLiteral);
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
	public static class KeywordContext extends ParserRuleContext {
		public TerminalNode Break() { return getToken(TypeScriptParser.Break, 0); }
		public TerminalNode Do() { return getToken(TypeScriptParser.Do, 0); }
		public TerminalNode Instanceof() { return getToken(TypeScriptParser.Instanceof, 0); }
		public TerminalNode Typeof() { return getToken(TypeScriptParser.Typeof, 0); }
		public TerminalNode Case() { return getToken(TypeScriptParser.Case, 0); }
		public TerminalNode Else() { return getToken(TypeScriptParser.Else, 0); }
		public TerminalNode New() { return getToken(TypeScriptParser.New, 0); }
		public TerminalNode Var() { return getToken(TypeScriptParser.Var, 0); }
		public TerminalNode Catch() { return getToken(TypeScriptParser.Catch, 0); }
		public TerminalNode Finally() { return getToken(TypeScriptParser.Finally, 0); }
		public TerminalNode Return() { return getToken(TypeScriptParser.Return, 0); }
		public TerminalNode Void() { return getToken(TypeScriptParser.Void, 0); }
		public TerminalNode Continue() { return getToken(TypeScriptParser.Continue, 0); }
		public TerminalNode For() { return getToken(TypeScriptParser.For, 0); }
		public TerminalNode Switch() { return getToken(TypeScriptParser.Switch, 0); }
		public TerminalNode While() { return getToken(TypeScriptParser.While, 0); }
		public TerminalNode Debugger() { return getToken(TypeScriptParser.Debugger, 0); }
		public TerminalNode Function_() { return getToken(TypeScriptParser.Function_, 0); }
		public TerminalNode This() { return getToken(TypeScriptParser.This, 0); }
		public TerminalNode With() { return getToken(TypeScriptParser.With, 0); }
		public TerminalNode Default() { return getToken(TypeScriptParser.Default, 0); }
		public TerminalNode If() { return getToken(TypeScriptParser.If, 0); }
		public TerminalNode Throw() { return getToken(TypeScriptParser.Throw, 0); }
		public TerminalNode Delete() { return getToken(TypeScriptParser.Delete, 0); }
		public TerminalNode In() { return getToken(TypeScriptParser.In, 0); }
		public TerminalNode Try() { return getToken(TypeScriptParser.Try, 0); }
		public TerminalNode ReadOnly() { return getToken(TypeScriptParser.ReadOnly, 0); }
		public TerminalNode Async() { return getToken(TypeScriptParser.Async, 0); }
		public TerminalNode From() { return getToken(TypeScriptParser.From, 0); }
		public TerminalNode Class() { return getToken(TypeScriptParser.Class, 0); }
		public TerminalNode Enum() { return getToken(TypeScriptParser.Enum, 0); }
		public TerminalNode Extends() { return getToken(TypeScriptParser.Extends, 0); }
		public TerminalNode Super() { return getToken(TypeScriptParser.Super, 0); }
		public TerminalNode Const() { return getToken(TypeScriptParser.Const, 0); }
		public TerminalNode Export() { return getToken(TypeScriptParser.Export, 0); }
		public TerminalNode Import() { return getToken(TypeScriptParser.Import, 0); }
		public TerminalNode Implements() { return getToken(TypeScriptParser.Implements, 0); }
		public TerminalNode Let() { return getToken(TypeScriptParser.Let, 0); }
		public TerminalNode Private() { return getToken(TypeScriptParser.Private, 0); }
		public TerminalNode Public() { return getToken(TypeScriptParser.Public, 0); }
		public TerminalNode Interface() { return getToken(TypeScriptParser.Interface, 0); }
		public TerminalNode Package() { return getToken(TypeScriptParser.Package, 0); }
		public TerminalNode Protected() { return getToken(TypeScriptParser.Protected, 0); }
		public TerminalNode Static() { return getToken(TypeScriptParser.Static, 0); }
		public TerminalNode Yield() { return getToken(TypeScriptParser.Yield, 0); }
		public TerminalNode Get() { return getToken(TypeScriptParser.Get, 0); }
		public TerminalNode Set() { return getToken(TypeScriptParser.Set, 0); }
		public TerminalNode Require() { return getToken(TypeScriptParser.Require, 0); }
		public TerminalNode TypeAlias() { return getToken(TypeScriptParser.TypeAlias, 0); }
		public TerminalNode String() { return getToken(TypeScriptParser.String, 0); }
		public TerminalNode Boolean() { return getToken(TypeScriptParser.Boolean, 0); }
		public TerminalNode Number() { return getToken(TypeScriptParser.Number, 0); }
		public TerminalNode Module() { return getToken(TypeScriptParser.Module, 0); }
		public KeywordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keyword; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterKeyword(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitKeyword(this);
		}
	}

	public final KeywordContext keyword() throws RecognitionException {
		KeywordContext _localctx = new KeywordContext(_ctx, getState());
		enterRule(_localctx, 280, RULE_keyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1672);
			_la = _input.LA(1);
			if ( !(((((_la - 62)) & ~0x3f) == 0 && ((1L << (_la - 62)) & 232990911905136639L) != 0)) ) {
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
	public static class GetterContext extends ParserRuleContext {
		public TerminalNode Get() { return getToken(TypeScriptParser.Get, 0); }
		public PropertyNameContext propertyName() {
			return getRuleContext(PropertyNameContext.class,0);
		}
		public GetterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_getter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterGetter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitGetter(this);
		}
	}

	public final GetterContext getter() throws RecognitionException {
		GetterContext _localctx = new GetterContext(_ctx, getState());
		enterRule(_localctx, 282, RULE_getter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1674);
			match(Get);
			setState(1675);
			propertyName();
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
	public static class SetterContext extends ParserRuleContext {
		public TerminalNode Set() { return getToken(TypeScriptParser.Set, 0); }
		public PropertyNameContext propertyName() {
			return getRuleContext(PropertyNameContext.class,0);
		}
		public SetterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterSetter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitSetter(this);
		}
	}

	public final SetterContext setter() throws RecognitionException {
		SetterContext _localctx = new SetterContext(_ctx, getState());
		enterRule(_localctx, 284, RULE_setter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1677);
			match(Set);
			setState(1678);
			propertyName();
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
	public static class EosContext extends ParserRuleContext {
		public TerminalNode SemiColon() { return getToken(TypeScriptParser.SemiColon, 0); }
		public TerminalNode EOF() { return getToken(TypeScriptParser.EOF, 0); }
		public EosContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eos; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).enterEos(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeScriptParserListener ) ((TypeScriptParserListener)listener).exitEos(this);
		}
	}

	public final EosContext eos() throws RecognitionException {
		EosContext _localctx = new EosContext(_ctx, getState());
		enterRule(_localctx, 286, RULE_eos);
		try {
			setState(1684);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,212,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1680);
				match(SemiColon);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1681);
				match(EOF);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1682);
				if (!(this.lineTerminatorAhead())) throw new FailedPredicateException(this, "this.lineTerminatorAhead()");
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1683);
				if (!(this.closeBrace())) throw new FailedPredicateException(this, "this.closeBrace()");
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 10:
			return unionOrIntersectionOrPrimaryType_sempred((UnionOrIntersectionOrPrimaryTypeContext)_localctx, predIndex);
		case 11:
			return primaryType_sempred((PrimaryTypeContext)_localctx, predIndex);
		case 22:
			return arrayType_sempred((ArrayTypeContext)_localctx, predIndex);
		case 57:
			return decoratorMemberExpression_sempred((DecoratorMemberExpressionContext)_localctx, predIndex);
		case 73:
			return expressionStatement_sempred((ExpressionStatementContext)_localctx, predIndex);
		case 75:
			return iterationStatement_sempred((IterationStatementContext)_localctx, predIndex);
		case 77:
			return continueStatement_sempred((ContinueStatementContext)_localctx, predIndex);
		case 78:
			return breakStatement_sempred((BreakStatementContext)_localctx, predIndex);
		case 79:
			return returnStatement_sempred((ReturnStatementContext)_localctx, predIndex);
		case 80:
			return yieldStatement_sempred((YieldStatementContext)_localctx, predIndex);
		case 88:
			return throwStatement_sempred((ThrowStatementContext)_localctx, predIndex);
		case 127:
			return singleExpression_sempred((SingleExpressionContext)_localctx, predIndex);
		case 143:
			return eos_sempred((EosContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean unionOrIntersectionOrPrimaryType_sempred(UnionOrIntersectionOrPrimaryTypeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		case 1:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean primaryType_sempred(PrimaryTypeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 5);
		case 3:
			return notLineTerminator();
		}
		return true;
	}
	private boolean arrayType_sempred(ArrayTypeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4:
			return notLineTerminator();
		}
		return true;
	}
	private boolean decoratorMemberExpression_sempred(DecoratorMemberExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean expressionStatement_sempred(ExpressionStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 6:
			return this.notOpenBraceAndNotFunction();
		}
		return true;
	}
	private boolean iterationStatement_sempred(IterationStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 7:
			return this.p("of");
		case 8:
			return this.p("of");
		}
		return true;
	}
	private boolean continueStatement_sempred(ContinueStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 9:
			return this.notLineTerminator();
		}
		return true;
	}
	private boolean breakStatement_sempred(BreakStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 10:
			return this.notLineTerminator();
		}
		return true;
	}
	private boolean returnStatement_sempred(ReturnStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 11:
			return this.notLineTerminator();
		}
		return true;
	}
	private boolean yieldStatement_sempred(YieldStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 12:
			return this.notLineTerminator();
		}
		return true;
	}
	private boolean throwStatement_sempred(ThrowStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 13:
			return this.notLineTerminator();
		}
		return true;
	}
	private boolean singleExpression_sempred(SingleExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 14:
			return precpred(_ctx, 29);
		case 15:
			return precpred(_ctx, 28);
		case 16:
			return precpred(_ctx, 27);
		case 17:
			return precpred(_ctx, 26);
		case 18:
			return precpred(_ctx, 25);
		case 19:
			return precpred(_ctx, 24);
		case 20:
			return precpred(_ctx, 23);
		case 21:
			return precpred(_ctx, 22);
		case 22:
			return precpred(_ctx, 21);
		case 23:
			return precpred(_ctx, 20);
		case 24:
			return precpred(_ctx, 19);
		case 25:
			return precpred(_ctx, 18);
		case 26:
			return precpred(_ctx, 17);
		case 27:
			return precpred(_ctx, 16);
		case 28:
			return precpred(_ctx, 15);
		case 29:
			return precpred(_ctx, 45);
		case 30:
			return precpred(_ctx, 44);
		case 31:
			return precpred(_ctx, 41);
		case 32:
			return precpred(_ctx, 40);
		case 33:
			return this.notLineTerminator();
		case 34:
			return precpred(_ctx, 39);
		case 35:
			return this.notLineTerminator();
		case 36:
			return precpred(_ctx, 14);
		case 37:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean eos_sempred(EosContext _localctx, int predIndex) {
		switch (predIndex) {
		case 38:
			return this.lineTerminatorAhead();
		case 39:
			return this.closeBrace();
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0086\u0697\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007"+
		"\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007"+
		"\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007"+
		"\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007"+
		"\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007"+
		"\'\u0002(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007"+
		",\u0002-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u0007"+
		"1\u00022\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u0007"+
		"6\u00027\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007"+
		";\u0002<\u0007<\u0002=\u0007=\u0002>\u0007>\u0002?\u0007?\u0002@\u0007"+
		"@\u0002A\u0007A\u0002B\u0007B\u0002C\u0007C\u0002D\u0007D\u0002E\u0007"+
		"E\u0002F\u0007F\u0002G\u0007G\u0002H\u0007H\u0002I\u0007I\u0002J\u0007"+
		"J\u0002K\u0007K\u0002L\u0007L\u0002M\u0007M\u0002N\u0007N\u0002O\u0007"+
		"O\u0002P\u0007P\u0002Q\u0007Q\u0002R\u0007R\u0002S\u0007S\u0002T\u0007"+
		"T\u0002U\u0007U\u0002V\u0007V\u0002W\u0007W\u0002X\u0007X\u0002Y\u0007"+
		"Y\u0002Z\u0007Z\u0002[\u0007[\u0002\\\u0007\\\u0002]\u0007]\u0002^\u0007"+
		"^\u0002_\u0007_\u0002`\u0007`\u0002a\u0007a\u0002b\u0007b\u0002c\u0007"+
		"c\u0002d\u0007d\u0002e\u0007e\u0002f\u0007f\u0002g\u0007g\u0002h\u0007"+
		"h\u0002i\u0007i\u0002j\u0007j\u0002k\u0007k\u0002l\u0007l\u0002m\u0007"+
		"m\u0002n\u0007n\u0002o\u0007o\u0002p\u0007p\u0002q\u0007q\u0002r\u0007"+
		"r\u0002s\u0007s\u0002t\u0007t\u0002u\u0007u\u0002v\u0007v\u0002w\u0007"+
		"w\u0002x\u0007x\u0002y\u0007y\u0002z\u0007z\u0002{\u0007{\u0002|\u0007"+
		"|\u0002}\u0007}\u0002~\u0007~\u0002\u007f\u0007\u007f\u0002\u0080\u0007"+
		"\u0080\u0002\u0081\u0007\u0081\u0002\u0082\u0007\u0082\u0002\u0083\u0007"+
		"\u0083\u0002\u0084\u0007\u0084\u0002\u0085\u0007\u0085\u0002\u0086\u0007"+
		"\u0086\u0002\u0087\u0007\u0087\u0002\u0088\u0007\u0088\u0002\u0089\u0007"+
		"\u0089\u0002\u008a\u0007\u008a\u0002\u008b\u0007\u008b\u0002\u008c\u0007"+
		"\u008c\u0002\u008d\u0007\u008d\u0002\u008e\u0007\u008e\u0002\u008f\u0007"+
		"\u008f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0003"+
		"\u0001\u0126\b\u0001\u0001\u0002\u0001\u0002\u0003\u0002\u012a\b\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003"+
		"\u0131\b\u0003\n\u0003\f\u0003\u0134\t\u0003\u0001\u0004\u0001\u0004\u0003"+
		"\u0004\u0138\b\u0004\u0001\u0004\u0003\u0004\u013b\b\u0004\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0003\u0006\u0142\b\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007"+
		"\u0149\b\u0007\n\u0007\f\u0007\u014c\t\u0007\u0001\b\u0001\b\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0003\t\u0155\b\t\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0005\n\u0160\b\n\n\n\f\n\u0163"+
		"\t\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0003\u000b\u0177\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0005\u000b\u017d\b\u000b\n\u000b\f\u000b\u0180\t\u000b\u0001\f"+
		"\u0001\f\u0001\r\u0001\r\u0003\r\u0186\b\r\u0001\u000e\u0001\u000e\u0003"+
		"\u000e\u018a\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u0199\b\u0010\u0001\u0011\u0001"+
		"\u0011\u0003\u0011\u019d\b\u0011\u0001\u0012\u0001\u0012\u0003\u0012\u01a1"+
		"\b\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0003\u0013\u01a7"+
		"\b\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0005\u0014\u01ac\b\u0014"+
		"\n\u0014\f\u0014\u01af\t\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015\u01b8\b\u0015\u0003"+
		"\u0015\u01ba\b\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0001"+
		"\u0018\u0001\u0018\u0005\u0018\u01c8\b\u0018\n\u0018\f\u0018\u01cb\t\u0018"+
		"\u0001\u0019\u0003\u0019\u01ce\b\u0019\u0001\u0019\u0001\u0019\u0003\u0019"+
		"\u01d2\b\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u001a"+
		"\u0001\u001a\u0003\u001a\u01da\b\u001a\u0001\u001a\u0001\u001a\u0003\u001a"+
		"\u01de\b\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c"+
		"\u0004\u001c\u01eb\b\u001c\u000b\u001c\f\u001c\u01ec\u0001\u001c\u0001"+
		"\u001c\u0003\u001c\u01f1\b\u001c\u0001\u001d\u0003\u001d\u01f4\b\u001d"+
		"\u0001\u001d\u0001\u001d\u0003\u001d\u01f8\b\u001d\u0001\u001d\u0003\u001d"+
		"\u01fb\b\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u01ff\b\u001d\u0001"+
		"\u001e\u0001\u001e\u0001\u001e\u0001\u001f\u0003\u001f\u0205\b\u001f\u0001"+
		"\u001f\u0001\u001f\u0003\u001f\u0209\b\u001f\u0001\u001f\u0001\u001f\u0003"+
		"\u001f\u020d\b\u001f\u0001 \u0001 \u0001 \u0001 \u0005 \u0213\b \n \f"+
		" \u0216\t \u0001 \u0001 \u0003 \u021a\b \u0003 \u021c\b \u0001!\u0001"+
		"!\u0001!\u0005!\u0221\b!\n!\f!\u0224\t!\u0001\"\u0001\"\u0003\"\u0228"+
		"\b\"\u0001#\u0003#\u022b\b#\u0001#\u0003#\u022e\b#\u0001#\u0001#\u0001"+
		"#\u0003#\u0233\b#\u0001#\u0003#\u0236\b#\u0001#\u0003#\u0239\b#\u0001"+
		"$\u0001$\u0001$\u0003$\u023e\b$\u0001%\u0003%\u0241\b%\u0001%\u0003%\u0244"+
		"\b%\u0001%\u0001%\u0003%\u0248\b%\u0001&\u0001&\u0001\'\u0001\'\u0003"+
		"\'\u024e\b\'\u0001(\u0001(\u0003(\u0252\b(\u0001(\u0001(\u0003(\u0256"+
		"\b(\u0001(\u0001(\u0003(\u025a\b(\u0001)\u0001)\u0001)\u0001)\u0001)\u0001"+
		")\u0001)\u0001*\u0001*\u0003*\u0265\b*\u0001*\u0001*\u0001+\u0001+\u0001"+
		"+\u0003+\u026c\b+\u0001+\u0001+\u0001+\u0001+\u0001,\u0003,\u0273\b,\u0001"+
		",\u0001,\u0001,\u0003,\u0278\b,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001"+
		",\u0003,\u0280\b,\u0001-\u0003-\u0283\b-\u0001-\u0003-\u0286\b-\u0001"+
		"-\u0001-\u0001-\u0003-\u028b\b-\u0001-\u0003-\u028e\b-\u0001-\u0001-\u0003"+
		"-\u0292\b-\u0001.\u0001.\u0001.\u0001/\u0001/\u0001/\u0005/\u029a\b/\n"+
		"/\f/\u029d\t/\u00010\u00030\u02a0\b0\u00010\u00010\u00010\u00010\u0003"+
		"0\u02a6\b0\u00010\u00010\u00011\u00011\u00031\u02ac\b1\u00012\u00012\u0001"+
		"2\u00052\u02b1\b2\n2\f2\u02b4\t2\u00013\u00013\u00013\u00033\u02b9\b3"+
		"\u00014\u00014\u00014\u00014\u00034\u02bf\b4\u00014\u00014\u00015\u0001"+
		"5\u00045\u02c5\b5\u000b5\f5\u02c6\u00015\u00055\u02ca\b5\n5\f5\u02cd\t"+
		"5\u00016\u00016\u00016\u00016\u00016\u00017\u00047\u02d5\b7\u000b7\f7"+
		"\u02d6\u00018\u00018\u00018\u00038\u02dc\b8\u00019\u00019\u00019\u0001"+
		"9\u00019\u00019\u00039\u02e4\b9\u00019\u00019\u00019\u00059\u02e9\b9\n"+
		"9\f9\u02ec\t9\u0001:\u0001:\u0001:\u0001;\u0003;\u02f2\b;\u0001;\u0001"+
		";\u0001<\u0003<\u02f7\b<\u0001<\u0001<\u0001=\u0001=\u0001=\u0001=\u0001"+
		"=\u0001=\u0001=\u0001=\u0001=\u0001=\u0001=\u0001=\u0001=\u0001=\u0001"+
		"=\u0001=\u0001=\u0001=\u0001=\u0001=\u0001=\u0001=\u0001=\u0001=\u0001"+
		"=\u0001=\u0001=\u0001=\u0001=\u0003=\u0318\b=\u0001>\u0001>\u0003>\u031c"+
		"\b>\u0001>\u0001>\u0001?\u0004?\u0321\b?\u000b?\f?\u0322\u0001@\u0001"+
		"@\u0001@\u0001@\u0003@\u0329\b@\u0001@\u0001@\u0001A\u0001A\u0001A\u0003"+
		"A\u0330\bA\u0001B\u0001B\u0003B\u0334\bB\u0001B\u0001B\u0003B\u0338\b"+
		"B\u0001B\u0001B\u0001B\u0001B\u0001C\u0001C\u0001C\u0003C\u0341\bC\u0001"+
		"C\u0001C\u0001C\u0001C\u0005C\u0347\bC\nC\fC\u034a\tC\u0001C\u0001C\u0001"+
		"D\u0001D\u0003D\u0350\bD\u0001D\u0001D\u0003D\u0354\bD\u0001E\u0001E\u0003"+
		"E\u0358\bE\u0001E\u0001E\u0003E\u035c\bE\u0001E\u0003E\u035f\bE\u0001"+
		"E\u0003E\u0362\bE\u0001E\u0003E\u0365\bE\u0001E\u0001E\u0003E\u0369\b"+
		"E\u0001E\u0001E\u0003E\u036d\bE\u0001E\u0001E\u0003E\u0371\bE\u0003E\u0373"+
		"\bE\u0001F\u0001F\u0001F\u0005F\u0378\bF\nF\fF\u037b\tF\u0001G\u0001G"+
		"\u0001G\u0003G\u0380\bG\u0001G\u0003G\u0383\bG\u0001G\u0003G\u0386\bG"+
		"\u0001G\u0001G\u0003G\u038a\bG\u0001G\u0003G\u038d\bG\u0001H\u0001H\u0001"+
		"I\u0001I\u0001I\u0003I\u0394\bI\u0001J\u0001J\u0001J\u0001J\u0001J\u0001"+
		"J\u0001J\u0003J\u039d\bJ\u0001K\u0001K\u0001K\u0001K\u0001K\u0001K\u0001"+
		"K\u0001K\u0001K\u0001K\u0001K\u0001K\u0001K\u0001K\u0001K\u0001K\u0001"+
		"K\u0003K\u03b0\bK\u0001K\u0001K\u0003K\u03b4\bK\u0001K\u0001K\u0003K\u03b8"+
		"\bK\u0001K\u0001K\u0001K\u0001K\u0001K\u0001K\u0001K\u0001K\u0003K\u03c2"+
		"\bK\u0001K\u0001K\u0003K\u03c6\bK\u0001K\u0001K\u0001K\u0001K\u0001K\u0001"+
		"K\u0001K\u0001K\u0001K\u0003K\u03d1\bK\u0001K\u0001K\u0001K\u0001K\u0001"+
		"K\u0001K\u0001K\u0001K\u0001K\u0001K\u0001K\u0003K\u03de\bK\u0001K\u0001"+
		"K\u0001K\u0001K\u0003K\u03e4\bK\u0001L\u0001L\u0001M\u0001M\u0001M\u0003"+
		"M\u03eb\bM\u0001M\u0001M\u0001N\u0001N\u0001N\u0003N\u03f2\bN\u0001N\u0001"+
		"N\u0001O\u0001O\u0001O\u0003O\u03f9\bO\u0001O\u0001O\u0001P\u0001P\u0001"+
		"P\u0003P\u0400\bP\u0001P\u0001P\u0001Q\u0001Q\u0001Q\u0001Q\u0001Q\u0001"+
		"Q\u0001R\u0001R\u0001R\u0001R\u0001R\u0001R\u0001S\u0001S\u0003S\u0412"+
		"\bS\u0001S\u0001S\u0003S\u0416\bS\u0003S\u0418\bS\u0001S\u0001S\u0001"+
		"T\u0004T\u041d\bT\u000bT\fT\u041e\u0001U\u0001U\u0001U\u0001U\u0003U\u0425"+
		"\bU\u0001V\u0001V\u0001V\u0003V\u042a\bV\u0001W\u0001W\u0001W\u0001W\u0001"+
		"X\u0001X\u0001X\u0001X\u0001X\u0001Y\u0001Y\u0001Y\u0001Y\u0003Y\u0439"+
		"\bY\u0001Y\u0003Y\u043c\bY\u0001Z\u0001Z\u0001Z\u0001Z\u0001Z\u0001Z\u0001"+
		"[\u0001[\u0001[\u0001\\\u0001\\\u0001\\\u0001]\u0001]\u0001]\u0001]\u0001"+
		"]\u0001]\u0001]\u0001]\u0003]\u0452\b]\u0001^\u0003^\u0455\b^\u0001^\u0001"+
		"^\u0003^\u0459\b^\u0003^\u045b\b^\u0001^\u0003^\u045e\b^\u0001^\u0001"+
		"^\u0001^\u0003^\u0463\b^\u0001^\u0001^\u0001^\u0001_\u0003_\u0469\b_\u0001"+
		"_\u0003_\u046c\b_\u0001`\u0001`\u0005`\u0470\b`\n`\f`\u0473\t`\u0001`"+
		"\u0001`\u0001a\u0001a\u0001a\u0001b\u0001b\u0001b\u0001c\u0001c\u0003"+
		"c\u047f\bc\u0001c\u0001c\u0001c\u0003c\u0484\bc\u0001d\u0001d\u0001d\u0003"+
		"d\u0489\bd\u0001d\u0003d\u048c\bd\u0001d\u0003d\u048f\bd\u0001d\u0001"+
		"d\u0001d\u0001d\u0001d\u0001d\u0001d\u0001d\u0001d\u0001d\u0003d\u049b"+
		"\bd\u0001d\u0001d\u0001d\u0003d\u04a0\bd\u0001d\u0003d\u04a3\bd\u0001"+
		"e\u0003e\u04a6\be\u0001e\u0003e\u04a9\be\u0001e\u0003e\u04ac\be\u0001"+
		"e\u0003e\u04af\be\u0001f\u0001f\u0001f\u0001g\u0003g\u04b5\bg\u0001g\u0001"+
		"g\u0001g\u0003g\u04ba\bg\u0001g\u0001g\u0001g\u0001g\u0001g\u0001h\u0001"+
		"h\u0001h\u0003h\u04c4\bh\u0001h\u0001h\u0003h\u04c8\bh\u0001h\u0001h\u0001"+
		"h\u0001h\u0001h\u0001i\u0001i\u0001i\u0001i\u0005i\u04d3\bi\ni\fi\u04d6"+
		"\ti\u0001i\u0003i\u04d9\bi\u0001i\u0001i\u0001j\u0001j\u0001j\u0001k\u0001"+
		"k\u0001k\u0001k\u0005k\u04e4\bk\nk\fk\u04e7\tk\u0001k\u0003k\u04ea\bk"+
		"\u0001k\u0001k\u0001l\u0001l\u0001l\u0001l\u0001l\u0003l\u04f3\bl\u0001"+
		"l\u0001l\u0001l\u0001l\u0001l\u0001m\u0001m\u0001m\u0005m\u04fd\bm\nm"+
		"\fm\u0500\tm\u0001m\u0001m\u0003m\u0504\bm\u0001m\u0001m\u0001m\u0001"+
		"m\u0001m\u0003m\u050b\bm\u0003m\u050d\bm\u0001n\u0003n\u0510\bn\u0001"+
		"n\u0003n\u0513\bn\u0001n\u0001n\u0003n\u0517\bn\u0001n\u0003n\u051a\b"+
		"n\u0001n\u0001n\u0003n\u051e\bn\u0001o\u0001o\u0001o\u0003o\u0523\bo\u0001"+
		"p\u0003p\u0526\bp\u0001q\u0004q\u0529\bq\u000bq\fq\u052a\u0001r\u0001"+
		"r\u0003r\u052f\br\u0001r\u0001r\u0001s\u0001s\u0004s\u0535\bs\u000bs\f"+
		"s\u0536\u0001s\u0005s\u053a\bs\ns\fs\u053d\ts\u0001t\u0003t\u0540\bt\u0001"+
		"t\u0001t\u0003t\u0544\bt\u0001t\u0003t\u0547\bt\u0001u\u0001u\u0001u\u0001"+
		"u\u0005u\u054d\bu\nu\fu\u0550\tu\u0001u\u0003u\u0553\bu\u0003u\u0555\b"+
		"u\u0001u\u0001u\u0001v\u0001v\u0001v\u0001v\u0001v\u0001v\u0001v\u0001"+
		"v\u0001v\u0001v\u0001v\u0001v\u0001v\u0001v\u0001v\u0003v\u0568\bv\u0001"+
		"w\u0001w\u0001w\u0001w\u0003w\u056e\bw\u0001w\u0001w\u0001w\u0001w\u0001"+
		"x\u0001x\u0001x\u0001x\u0003x\u0578\bx\u0001x\u0003x\u057b\bx\u0001x\u0001"+
		"x\u0001x\u0001x\u0001x\u0001y\u0001y\u0001y\u0003y\u0585\by\u0001z\u0001"+
		"z\u0001z\u0003z\u058a\bz\u0003z\u058c\bz\u0001z\u0001z\u0001{\u0001{\u0001"+
		"{\u0005{\u0593\b{\n{\f{\u0596\t{\u0001|\u0003|\u0599\b|\u0001|\u0001|"+
		"\u0003|\u059d\b|\u0001}\u0001}\u0001}\u0005}\u05a2\b}\n}\f}\u05a5\t}\u0001"+
		"~\u0001~\u0003~\u05a9\b~\u0001~\u0001~\u0003~\u05ad\b~\u0001~\u0001~\u0003"+
		"~\u05b1\b~\u0001~\u0001~\u0001~\u0001~\u0001\u007f\u0001\u007f\u0001\u007f"+
		"\u0001\u007f\u0001\u007f\u0001\u007f\u0003\u007f\u05bd\b\u007f\u0001\u007f"+
		"\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0003\u007f\u05c4\b\u007f"+
		"\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f"+
		"\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f"+
		"\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f"+
		"\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f"+
		"\u0001\u007f\u0003\u007f\u05df\b\u007f\u0001\u007f\u0001\u007f\u0001\u007f"+
		"\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f"+
		"\u0001\u007f\u0003\u007f\u05eb\b\u007f\u0003\u007f\u05ed\b\u007f\u0001"+
		"\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0003\u007f\u0627\b\u007f\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0003\u007f\u062c\b\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0005\u007f\u063b\b\u007f\n"+
		"\u007f\f\u007f\u063e\t\u007f\u0001\u0080\u0001\u0080\u0001\u0080\u0003"+
		"\u0080\u0643\b\u0080\u0001\u0080\u0003\u0080\u0646\b\u0080\u0001\u0081"+
		"\u0003\u0081\u0649\b\u0081\u0001\u0081\u0001\u0081\u0003\u0081\u064d\b"+
		"\u0081\u0001\u0081\u0001\u0081\u0001\u0081\u0001\u0082\u0001\u0082\u0001"+
		"\u0082\u0003\u0082\u0655\b\u0082\u0001\u0082\u0003\u0082\u0658\b\u0082"+
		"\u0001\u0083\u0001\u0083\u0001\u0083\u0001\u0083\u0001\u0083\u0003\u0083"+
		"\u065f\b\u0083\u0001\u0084\u0001\u0084\u0001\u0085\u0001\u0085\u0001\u0085"+
		"\u0001\u0085\u0001\u0085\u0001\u0085\u0003\u0085\u0669\b\u0085\u0001\u0086"+
		"\u0001\u0086\u0005\u0086\u066d\b\u0086\n\u0086\f\u0086\u0670\t\u0086\u0001"+
		"\u0086\u0001\u0086\u0001\u0087\u0001\u0087\u0001\u0087\u0001\u0087\u0001"+
		"\u0087\u0001\u0087\u0003\u0087\u067a\b\u0087\u0001\u0088\u0001\u0088\u0001"+
		"\u0089\u0001\u0089\u0003\u0089\u0680\b\u0089\u0001\u008a\u0001\u008a\u0001"+
		"\u008b\u0001\u008b\u0001\u008b\u0003\u008b\u0687\b\u008b\u0001\u008c\u0001"+
		"\u008c\u0001\u008d\u0001\u008d\u0001\u008d\u0001\u008e\u0001\u008e\u0001"+
		"\u008e\u0001\u008f\u0001\u008f\u0001\u008f\u0001\u008f\u0003\u008f\u0695"+
		"\b\u008f\u0001\u008f\u0000\u0004\u0014\u0016r\u00fe\u0090\u0000\u0002"+
		"\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e"+
		" \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086"+
		"\u0088\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e"+
		"\u00a0\u00a2\u00a4\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6"+
		"\u00b8\u00ba\u00bc\u00be\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc\u00ce"+
		"\u00d0\u00d2\u00d4\u00d6\u00d8\u00da\u00dc\u00de\u00e0\u00e2\u00e4\u00e6"+
		"\u00e8\u00ea\u00ec\u00ee\u00f0\u00f2\u00f4\u00f6\u00f8\u00fa\u00fc\u00fe"+
		"\u0100\u0102\u0104\u0106\u0108\u010a\u010c\u010e\u0110\u0112\u0114\u0116"+
		"\u0118\u011a\u011c\u011e\u0000\u000f\u0002\u0000IIlp\u0001\u0000\u000b"+
		"\f\u0002\u0000efii\u0002\u0000mmoo\u0003\u0000EE``dd\u0002\u0000\r\r\u000f"+
		"\u000f\u0001\u0000\u0018\u001a\u0001\u0000\u0014\u0015\u0001\u0000\u001b"+
		"\u001d\u0001\u0000\u001e!\u0001\u0000\"%\u0001\u0000+5\u0001\u00009=\u0003"+
		"\u0000qqvv||\u0005\u0000>WYkmoqsvw\u0747\u0000\u0120\u0001\u0000\u0000"+
		"\u0000\u0002\u0125\u0001\u0000\u0000\u0000\u0004\u0127\u0001\u0000\u0000"+
		"\u0000\u0006\u012d\u0001\u0000\u0000\u0000\b\u013a\u0001\u0000\u0000\u0000"+
		"\n\u013c\u0001\u0000\u0000\u0000\f\u013f\u0001\u0000\u0000\u0000\u000e"+
		"\u0145\u0001\u0000\u0000\u0000\u0010\u014d\u0001\u0000\u0000\u0000\u0012"+
		"\u0154\u0001\u0000\u0000\u0000\u0014\u0156\u0001\u0000\u0000\u0000\u0016"+
		"\u0176\u0001\u0000\u0000\u0000\u0018\u0181\u0001\u0000\u0000\u0000\u001a"+
		"\u0183\u0001\u0000\u0000\u0000\u001c\u0189\u0001\u0000\u0000\u0000\u001e"+
		"\u018b\u0001\u0000\u0000\u0000 \u018f\u0001\u0000\u0000\u0000\"\u019c"+
		"\u0001\u0000\u0000\u0000$\u019e\u0001\u0000\u0000\u0000&\u01a4\u0001\u0000"+
		"\u0000\u0000(\u01a8\u0001\u0000\u0000\u0000*\u01b9\u0001\u0000\u0000\u0000"+
		",\u01bb\u0001\u0000\u0000\u0000.\u01c0\u0001\u0000\u0000\u00000\u01c4"+
		"\u0001\u0000\u0000\u00002\u01cd\u0001\u0000\u0000\u00004\u01d7\u0001\u0000"+
		"\u0000\u00006\u01e3\u0001\u0000\u0000\u00008\u01f0\u0001\u0000\u0000\u0000"+
		":\u01f3\u0001\u0000\u0000\u0000<\u0200\u0001\u0000\u0000\u0000>\u0204"+
		"\u0001\u0000\u0000\u0000@\u021b\u0001\u0000\u0000\u0000B\u021d\u0001\u0000"+
		"\u0000\u0000D\u0227\u0001\u0000\u0000\u0000F\u022a\u0001\u0000\u0000\u0000"+
		"H\u023a\u0001\u0000\u0000\u0000J\u0240\u0001\u0000\u0000\u0000L\u0249"+
		"\u0001\u0000\u0000\u0000N\u024d\u0001\u0000\u0000\u0000P\u024f\u0001\u0000"+
		"\u0000\u0000R\u025b\u0001\u0000\u0000\u0000T\u0262\u0001\u0000\u0000\u0000"+
		"V\u0268\u0001\u0000\u0000\u0000X\u0272\u0001\u0000\u0000\u0000Z\u0282"+
		"\u0001\u0000\u0000\u0000\\\u0293\u0001\u0000\u0000\u0000^\u0296\u0001"+
		"\u0000\u0000\u0000`\u029f\u0001\u0000\u0000\u0000b\u02a9\u0001\u0000\u0000"+
		"\u0000d\u02ad\u0001\u0000\u0000\u0000f\u02b5\u0001\u0000\u0000\u0000h"+
		"\u02ba\u0001\u0000\u0000\u0000j\u02c2\u0001\u0000\u0000\u0000l\u02ce\u0001"+
		"\u0000\u0000\u0000n\u02d4\u0001\u0000\u0000\u0000p\u02d8\u0001\u0000\u0000"+
		"\u0000r\u02e3\u0001\u0000\u0000\u0000t\u02ed\u0001\u0000\u0000\u0000v"+
		"\u02f1\u0001\u0000\u0000\u0000x\u02f6\u0001\u0000\u0000\u0000z\u0317\u0001"+
		"\u0000\u0000\u0000|\u0319\u0001\u0000\u0000\u0000~\u0320\u0001\u0000\u0000"+
		"\u0000\u0080\u0324\u0001\u0000\u0000\u0000\u0082\u032c\u0001\u0000\u0000"+
		"\u0000\u0084\u0333\u0001\u0000\u0000\u0000\u0086\u0340\u0001\u0000\u0000"+
		"\u0000\u0088\u034d\u0001\u0000\u0000\u0000\u008a\u0372\u0001\u0000\u0000"+
		"\u0000\u008c\u0374\u0001\u0000\u0000\u0000\u008e\u037f\u0001\u0000\u0000"+
		"\u0000\u0090\u038e\u0001\u0000\u0000\u0000\u0092\u0390\u0001\u0000\u0000"+
		"\u0000\u0094\u0395\u0001\u0000\u0000\u0000\u0096\u03e3\u0001\u0000\u0000"+
		"\u0000\u0098\u03e5\u0001\u0000\u0000\u0000\u009a\u03e7\u0001\u0000\u0000"+
		"\u0000\u009c\u03ee\u0001\u0000\u0000\u0000\u009e\u03f5\u0001\u0000\u0000"+
		"\u0000\u00a0\u03fc\u0001\u0000\u0000\u0000\u00a2\u0403\u0001\u0000\u0000"+
		"\u0000\u00a4\u0409\u0001\u0000\u0000\u0000\u00a6\u040f\u0001\u0000\u0000"+
		"\u0000\u00a8\u041c\u0001\u0000\u0000\u0000\u00aa\u0420\u0001\u0000\u0000"+
		"\u0000\u00ac\u0426\u0001\u0000\u0000\u0000\u00ae\u042b\u0001\u0000\u0000"+
		"\u0000\u00b0\u042f\u0001\u0000\u0000\u0000\u00b2\u0434\u0001\u0000\u0000"+
		"\u0000\u00b4\u043d\u0001\u0000\u0000\u0000\u00b6\u0443\u0001\u0000\u0000"+
		"\u0000\u00b8\u0446\u0001\u0000\u0000\u0000\u00ba\u0449\u0001\u0000\u0000"+
		"\u0000\u00bc\u0454\u0001\u0000\u0000\u0000\u00be\u0468\u0001\u0000\u0000"+
		"\u0000\u00c0\u046d\u0001\u0000\u0000\u0000\u00c2\u0476\u0001\u0000\u0000"+
		"\u0000\u00c4\u0479\u0001\u0000\u0000\u0000\u00c6\u0483\u0001\u0000\u0000"+
		"\u0000\u00c8\u04a2\u0001\u0000\u0000\u0000\u00ca\u04a5\u0001\u0000\u0000"+
		"\u0000\u00cc\u04b0\u0001\u0000\u0000\u0000\u00ce\u04b4\u0001\u0000\u0000"+
		"\u0000\u00d0\u04c0\u0001\u0000\u0000\u0000\u00d2\u04ce\u0001\u0000\u0000"+
		"\u0000\u00d4\u04dc\u0001\u0000\u0000\u0000\u00d6\u04df\u0001\u0000\u0000"+
		"\u0000\u00d8\u04ed\u0001\u0000\u0000\u0000\u00da\u050c\u0001\u0000\u0000"+
		"\u0000\u00dc\u050f\u0001\u0000\u0000\u0000\u00de\u051f\u0001\u0000\u0000"+
		"\u0000\u00e0\u0525\u0001\u0000\u0000\u0000\u00e2\u0528\u0001\u0000\u0000"+
		"\u0000\u00e4\u052c\u0001\u0000\u0000\u0000\u00e6\u0532\u0001\u0000\u0000"+
		"\u0000\u00e8\u053f\u0001\u0000\u0000\u0000\u00ea\u0548\u0001\u0000\u0000"+
		"\u0000\u00ec\u0567\u0001\u0000\u0000\u0000\u00ee\u0569\u0001\u0000\u0000"+
		"\u0000\u00f0\u0573\u0001\u0000\u0000\u0000\u00f2\u0584\u0001\u0000\u0000"+
		"\u0000\u00f4\u0586\u0001\u0000\u0000\u0000\u00f6\u058f\u0001\u0000\u0000"+
		"\u0000\u00f8\u0598\u0001\u0000\u0000\u0000\u00fa\u059e\u0001\u0000\u0000"+
		"\u0000\u00fc\u05a6\u0001\u0000\u0000\u0000\u00fe\u05ec\u0001\u0000\u0000"+
		"\u0000\u0100\u0645\u0001\u0000\u0000\u0000\u0102\u0648\u0001\u0000\u0000"+
		"\u0000\u0104\u0657\u0001\u0000\u0000\u0000\u0106\u065e\u0001\u0000\u0000"+
		"\u0000\u0108\u0660\u0001\u0000\u0000\u0000\u010a\u0668\u0001\u0000\u0000"+
		"\u0000\u010c\u066a\u0001\u0000\u0000\u0000\u010e\u0679\u0001\u0000\u0000"+
		"\u0000\u0110\u067b\u0001\u0000\u0000\u0000\u0112\u067f\u0001\u0000\u0000"+
		"\u0000\u0114\u0681\u0001\u0000\u0000\u0000\u0116\u0686\u0001\u0000\u0000"+
		"\u0000\u0118\u0688\u0001\u0000\u0000\u0000\u011a\u068a\u0001\u0000\u0000"+
		"\u0000\u011c\u068d\u0001\u0000\u0000\u0000\u011e\u0694\u0001\u0000\u0000"+
		"\u0000\u0120\u0121\u0005\r\u0000\u0000\u0121\u0122\u0003\u00fe\u007f\u0000"+
		"\u0122\u0001\u0001\u0000\u0000\u0000\u0123\u0126\u0003\u00e4r\u0000\u0124"+
		"\u0126\u0003\u00eau\u0000\u0125\u0123\u0001\u0000\u0000\u0000\u0125\u0124"+
		"\u0001\u0000\u0000\u0000\u0126\u0003\u0001\u0000\u0000\u0000\u0127\u0129"+
		"\u0005\u001e\u0000\u0000\u0128\u012a\u0003\u0006\u0003\u0000\u0129\u0128"+
		"\u0001\u0000\u0000\u0000\u0129\u012a\u0001\u0000\u0000\u0000\u012a\u012b"+
		"\u0001\u0000\u0000\u0000\u012b\u012c\u0005\u001f\u0000\u0000\u012c\u0005"+
		"\u0001\u0000\u0000\u0000\u012d\u0132\u0003\b\u0004\u0000\u012e\u012f\u0005"+
		"\f\u0000\u0000\u012f\u0131\u0003\b\u0004\u0000\u0130\u012e\u0001\u0000"+
		"\u0000\u0000\u0131\u0134\u0001\u0000\u0000\u0000\u0132\u0130\u0001\u0000"+
		"\u0000\u0000\u0132\u0133\u0001\u0000\u0000\u0000\u0133\u0007\u0001\u0000"+
		"\u0000\u0000\u0134\u0132\u0001\u0000\u0000\u0000\u0135\u0137\u0005|\u0000"+
		"\u0000\u0136\u0138\u0003\n\u0005\u0000\u0137\u0136\u0001\u0000\u0000\u0000"+
		"\u0137\u0138\u0001\u0000\u0000\u0000\u0138\u013b\u0001\u0000\u0000\u0000"+
		"\u0139\u013b\u0003\u0004\u0002\u0000\u013a\u0135\u0001\u0000\u0000\u0000"+
		"\u013a\u0139\u0001\u0000\u0000\u0000\u013b\t\u0001\u0000\u0000\u0000\u013c"+
		"\u013d\u0005^\u0000\u0000\u013d\u013e\u0003\u0012\t\u0000\u013e\u000b"+
		"\u0001\u0000\u0000\u0000\u013f\u0141\u0005\u001e\u0000\u0000\u0140\u0142"+
		"\u0003\u000e\u0007\u0000\u0141\u0140\u0001\u0000\u0000\u0000\u0141\u0142"+
		"\u0001\u0000\u0000\u0000\u0142\u0143\u0001\u0000\u0000\u0000\u0143\u0144"+
		"\u0005\u001f\u0000\u0000\u0144\r\u0001\u0000\u0000\u0000\u0145\u014a\u0003"+
		"\u0010\b\u0000\u0146\u0147\u0005\f\u0000\u0000\u0147\u0149\u0003\u0010"+
		"\b\u0000\u0148\u0146\u0001\u0000\u0000\u0000\u0149\u014c\u0001\u0000\u0000"+
		"\u0000\u014a\u0148\u0001\u0000\u0000\u0000\u014a\u014b\u0001\u0000\u0000"+
		"\u0000\u014b\u000f\u0001\u0000\u0000\u0000\u014c\u014a\u0001\u0000\u0000"+
		"\u0000\u014d\u014e\u0003\u0012\t\u0000\u014e\u0011\u0001\u0000\u0000\u0000"+
		"\u014f\u0155\u0003\u0014\n\u0000\u0150\u0155\u00032\u0019\u0000\u0151"+
		"\u0155\u00034\u001a\u0000\u0152\u0155\u0003\u001e\u000f\u0000\u0153\u0155"+
		"\u0005}\u0000\u0000\u0154\u014f\u0001\u0000\u0000\u0000\u0154\u0150\u0001"+
		"\u0000\u0000\u0000\u0154\u0151\u0001\u0000\u0000\u0000\u0154\u0152\u0001"+
		"\u0000\u0000\u0000\u0154\u0153\u0001\u0000\u0000\u0000\u0155\u0013\u0001"+
		"\u0000\u0000\u0000\u0156\u0157\u0006\n\uffff\uffff\u0000\u0157\u0158\u0003"+
		"\u0016\u000b\u0000\u0158\u0161\u0001\u0000\u0000\u0000\u0159\u015a\n\u0003"+
		"\u0000\u0000\u015a\u015b\u0005(\u0000\u0000\u015b\u0160\u0003\u0014\n"+
		"\u0004\u015c\u015d\n\u0002\u0000\u0000\u015d\u015e\u0005&\u0000\u0000"+
		"\u015e\u0160\u0003\u0014\n\u0003\u015f\u0159\u0001\u0000\u0000\u0000\u015f"+
		"\u015c\u0001\u0000\u0000\u0000\u0160\u0163\u0001\u0000\u0000\u0000\u0161"+
		"\u015f\u0001\u0000\u0000\u0000\u0161\u0162\u0001\u0000\u0000\u0000\u0162"+
		"\u0015\u0001\u0000\u0000\u0000\u0163\u0161\u0001\u0000\u0000\u0000\u0164"+
		"\u0165\u0006\u000b\uffff\uffff\u0000\u0165\u0166\u0005\u0006\u0000\u0000"+
		"\u0166\u0167\u0003\u0012\t\u0000\u0167\u0168\u0005\u0007\u0000\u0000\u0168"+
		"\u0177\u0001\u0000\u0000\u0000\u0169\u0177\u0003\u0018\f\u0000\u016a\u0177"+
		"\u0003\u001a\r\u0000\u016b\u0177\u0003$\u0012\u0000\u016c\u016d\u0005"+
		"\u0004\u0000\u0000\u016d\u016e\u00030\u0018\u0000\u016e\u016f\u0005\u0005"+
		"\u0000\u0000\u016f\u0177\u0001\u0000\u0000\u0000\u0170\u0177\u00036\u001b"+
		"\u0000\u0171\u0177\u0005P\u0000\u0000\u0172\u0173\u0003\u001a\r\u0000"+
		"\u0173\u0174\u0005z\u0000\u0000\u0174\u0175\u0003\u0016\u000b\u0001\u0175"+
		"\u0177\u0001\u0000\u0000\u0000\u0176\u0164\u0001\u0000\u0000\u0000\u0176"+
		"\u0169\u0001\u0000\u0000\u0000\u0176\u016a\u0001\u0000\u0000\u0000\u0176"+
		"\u016b\u0001\u0000\u0000\u0000\u0176\u016c\u0001\u0000\u0000\u0000\u0176"+
		"\u0170\u0001\u0000\u0000\u0000\u0176\u0171\u0001\u0000\u0000\u0000\u0176"+
		"\u0172\u0001\u0000\u0000\u0000\u0177\u017e\u0001\u0000\u0000\u0000\u0178"+
		"\u0179\n\u0005\u0000\u0000\u0179\u017a\u0004\u000b\u0003\u0000\u017a\u017b"+
		"\u0005\u0004\u0000\u0000\u017b\u017d\u0005\u0005\u0000\u0000\u017c\u0178"+
		"\u0001\u0000\u0000\u0000\u017d\u0180\u0001\u0000\u0000\u0000\u017e\u017c"+
		"\u0001\u0000\u0000\u0000\u017e\u017f\u0001\u0000\u0000\u0000\u017f\u0017"+
		"\u0001\u0000\u0000\u0000\u0180\u017e\u0001\u0000\u0000\u0000\u0181\u0182"+
		"\u0007\u0000\u0000\u0000\u0182\u0019\u0001\u0000\u0000\u0000\u0183\u0185"+
		"\u0003\"\u0011\u0000\u0184\u0186\u0003\u001c\u000e\u0000\u0185\u0184\u0001"+
		"\u0000\u0000\u0000\u0185\u0186\u0001\u0000\u0000\u0000\u0186\u001b\u0001"+
		"\u0000\u0000\u0000\u0187\u018a\u0003 \u0010\u0000\u0188\u018a\u0003\u001e"+
		"\u000f\u0000\u0189\u0187\u0001\u0000\u0000\u0000\u0189\u0188\u0001\u0000"+
		"\u0000\u0000\u018a\u001d\u0001\u0000\u0000\u0000\u018b\u018c\u0005\u001e"+
		"\u0000\u0000\u018c\u018d\u0003\u000e\u0007\u0000\u018d\u018e\u0005\u001f"+
		"\u0000\u0000\u018e\u001f\u0001\u0000\u0000\u0000\u018f\u0190\u0005\u001e"+
		"\u0000\u0000\u0190\u0191\u0003\u000e\u0007\u0000\u0191\u0192\u0005\u001e"+
		"\u0000\u0000\u0192\u0198\u0003\u000e\u0007\u0000\u0193\u0194\u0005\u001f"+
		"\u0000\u0000\u0194\u0195\u0003\u0002\u0001\u0000\u0195\u0196\u0005\u001f"+
		"\u0000\u0000\u0196\u0199\u0001\u0000\u0000\u0000\u0197\u0199\u0005\u001b"+
		"\u0000\u0000\u0198\u0193\u0001\u0000\u0000\u0000\u0198\u0197\u0001\u0000"+
		"\u0000\u0000\u0199!\u0001\u0000\u0000\u0000\u019a\u019d\u0005|\u0000\u0000"+
		"\u019b\u019d\u0003j5\u0000\u019c\u019a\u0001\u0000\u0000\u0000\u019c\u019b"+
		"\u0001\u0000\u0000\u0000\u019d#\u0001\u0000\u0000\u0000\u019e\u01a0\u0005"+
		"\b\u0000\u0000\u019f\u01a1\u0003&\u0013\u0000\u01a0\u019f\u0001\u0000"+
		"\u0000\u0000\u01a0\u01a1\u0001\u0000\u0000\u0000\u01a1\u01a2\u0001\u0000"+
		"\u0000\u0000\u01a2\u01a3\u0005\n\u0000\u0000\u01a3%\u0001\u0000\u0000"+
		"\u0000\u01a4\u01a6\u0003(\u0014\u0000\u01a5\u01a7\u0007\u0001\u0000\u0000"+
		"\u01a6\u01a5\u0001\u0000\u0000\u0000\u01a6\u01a7\u0001\u0000\u0000\u0000"+
		"\u01a7\'\u0001\u0000\u0000\u0000\u01a8\u01ad\u0003*\u0015\u0000\u01a9"+
		"\u01aa\u0007\u0001\u0000\u0000\u01aa\u01ac\u0003*\u0015\u0000\u01ab\u01a9"+
		"\u0001\u0000\u0000\u0000\u01ac\u01af\u0001\u0000\u0000\u0000\u01ad\u01ab"+
		"\u0001\u0000\u0000\u0000\u01ad\u01ae\u0001\u0000\u0000\u0000\u01ae)\u0001"+
		"\u0000\u0000\u0000\u01af\u01ad\u0001\u0000\u0000\u0000\u01b0\u01ba\u0003"+
		":\u001d\u0000\u01b1\u01ba\u0003>\u001f\u0000\u01b2\u01ba\u0003P(\u0000"+
		"\u01b3\u01ba\u0003R)\u0000\u01b4\u01b7\u0003T*\u0000\u01b5\u01b6\u0005"+
		"6\u0000\u0000\u01b6\u01b8\u0003\u0012\t\u0000\u01b7\u01b5\u0001\u0000"+
		"\u0000\u0000\u01b7\u01b8\u0001\u0000\u0000\u0000\u01b8\u01ba\u0001\u0000"+
		"\u0000\u0000\u01b9\u01b0\u0001\u0000\u0000\u0000\u01b9\u01b1\u0001\u0000"+
		"\u0000\u0000\u01b9\u01b2\u0001\u0000\u0000\u0000\u01b9\u01b3\u0001\u0000"+
		"\u0000\u0000\u01b9\u01b4\u0001\u0000\u0000\u0000\u01ba+\u0001\u0000\u0000"+
		"\u0000\u01bb\u01bc\u0003\u0016\u000b\u0000\u01bc\u01bd\u0004\u0016\u0004"+
		"\u0000\u01bd\u01be\u0005\u0004\u0000\u0000\u01be\u01bf\u0005\u0005\u0000"+
		"\u0000\u01bf-\u0001\u0000\u0000\u0000\u01c0\u01c1\u0005\u0004\u0000\u0000"+
		"\u01c1\u01c2\u00030\u0018\u0000\u01c2\u01c3\u0005\u0005\u0000\u0000\u01c3"+
		"/\u0001\u0000\u0000\u0000\u01c4\u01c9\u0003\u0012\t\u0000\u01c5\u01c6"+
		"\u0005\f\u0000\u0000\u01c6\u01c8\u0003\u0012\t\u0000\u01c7\u01c5\u0001"+
		"\u0000\u0000\u0000\u01c8\u01cb\u0001\u0000\u0000\u0000\u01c9\u01c7\u0001"+
		"\u0000\u0000\u0000\u01c9\u01ca\u0001\u0000\u0000\u0000\u01ca1\u0001\u0000"+
		"\u0000\u0000\u01cb\u01c9\u0001\u0000\u0000\u0000\u01cc\u01ce\u0003\u0004"+
		"\u0002\u0000\u01cd\u01cc\u0001\u0000\u0000\u0000\u01cd\u01ce\u0001\u0000"+
		"\u0000\u0000\u01ce\u01cf\u0001\u0000\u0000\u0000\u01cf\u01d1\u0005\u0006"+
		"\u0000\u0000\u01d0\u01d2\u0003@ \u0000\u01d1\u01d0\u0001\u0000\u0000\u0000"+
		"\u01d1\u01d2\u0001\u0000\u0000\u0000\u01d2\u01d3\u0001\u0000\u0000\u0000"+
		"\u01d3\u01d4\u0005\u0007\u0000\u0000\u01d4\u01d5\u00056\u0000\u0000\u01d5"+
		"\u01d6\u0003\u0012\t\u0000\u01d63\u0001\u0000\u0000\u0000\u01d7\u01d9"+
		"\u0005D\u0000\u0000\u01d8\u01da\u0003\u0004\u0002\u0000\u01d9\u01d8\u0001"+
		"\u0000\u0000\u0000\u01d9\u01da\u0001\u0000\u0000\u0000\u01da\u01db\u0001"+
		"\u0000\u0000\u0000\u01db\u01dd\u0005\u0006\u0000\u0000\u01dc\u01de\u0003"+
		"@ \u0000\u01dd\u01dc\u0001\u0000\u0000\u0000\u01dd\u01de\u0001\u0000\u0000"+
		"\u0000\u01de\u01df\u0001\u0000\u0000\u0000\u01df\u01e0\u0005\u0007\u0000"+
		"\u0000\u01e0\u01e1\u00056\u0000\u0000\u01e1\u01e2\u0003\u0012\t\u0000"+
		"\u01e25\u0001\u0000\u0000\u0000\u01e3\u01e4\u0005A\u0000\u0000\u01e4\u01e5"+
		"\u00038\u001c\u0000\u01e57\u0001\u0000\u0000\u0000\u01e6\u01f1\u0005|"+
		"\u0000\u0000\u01e7\u01e8\u0003\u0112\u0089\u0000\u01e8\u01e9\u0005\u0011"+
		"\u0000\u0000\u01e9\u01eb\u0001\u0000\u0000\u0000\u01ea\u01e7\u0001\u0000"+
		"\u0000\u0000\u01eb\u01ec\u0001\u0000\u0000\u0000\u01ec\u01ea\u0001\u0000"+
		"\u0000\u0000\u01ec\u01ed\u0001\u0000\u0000\u0000\u01ed\u01ee\u0001\u0000"+
		"\u0000\u0000\u01ee\u01ef\u0003\u0112\u0089\u0000\u01ef\u01f1\u0001\u0000"+
		"\u0000\u0000\u01f0\u01e6\u0001\u0000\u0000\u0000\u01f0\u01ea\u0001\u0000"+
		"\u0000\u0000\u01f19\u0001\u0000\u0000\u0000\u01f2\u01f4\u0005Z\u0000\u0000"+
		"\u01f3\u01f2\u0001\u0000\u0000\u0000\u01f3\u01f4\u0001\u0000\u0000\u0000"+
		"\u01f4\u01f5\u0001\u0000\u0000\u0000\u01f5\u01f7\u0003\u00f2y\u0000\u01f6"+
		"\u01f8\u0005\u000e\u0000\u0000\u01f7\u01f6\u0001\u0000\u0000\u0000\u01f7"+
		"\u01f8\u0001\u0000\u0000\u0000\u01f8\u01fa\u0001\u0000\u0000\u0000\u01f9"+
		"\u01fb\u0003<\u001e\u0000\u01fa\u01f9\u0001\u0000\u0000\u0000\u01fa\u01fb"+
		"\u0001\u0000\u0000\u0000\u01fb\u01fe\u0001\u0000\u0000\u0000\u01fc\u01fd"+
		"\u00056\u0000\u0000\u01fd\u01ff\u0003\u0012\t\u0000\u01fe\u01fc\u0001"+
		"\u0000\u0000\u0000\u01fe\u01ff\u0001\u0000\u0000\u0000\u01ff;\u0001\u0000"+
		"\u0000\u0000\u0200\u0201\u0005\u000f\u0000\u0000\u0201\u0202\u0003\u0012"+
		"\t\u0000\u0202=\u0001\u0000\u0000\u0000\u0203\u0205\u0003\u0004\u0002"+
		"\u0000\u0204\u0203\u0001\u0000\u0000\u0000\u0204\u0205\u0001\u0000\u0000"+
		"\u0000\u0205\u0206\u0001\u0000\u0000\u0000\u0206\u0208\u0005\u0006\u0000"+
		"\u0000\u0207\u0209\u0003@ \u0000\u0208\u0207\u0001\u0000\u0000\u0000\u0208"+
		"\u0209\u0001\u0000\u0000\u0000\u0209\u020a\u0001\u0000\u0000\u0000\u020a"+
		"\u020c\u0005\u0007\u0000\u0000\u020b\u020d\u0003<\u001e\u0000\u020c\u020b"+
		"\u0001\u0000\u0000\u0000\u020c\u020d\u0001\u0000\u0000\u0000\u020d?\u0001"+
		"\u0000\u0000\u0000\u020e\u021c\u0003H$\u0000\u020f\u0214\u0003D\"\u0000"+
		"\u0210\u0211\u0005\f\u0000\u0000\u0211\u0213\u0003D\"\u0000\u0212\u0210"+
		"\u0001\u0000\u0000\u0000\u0213\u0216\u0001\u0000\u0000\u0000\u0214\u0212"+
		"\u0001\u0000\u0000\u0000\u0214\u0215\u0001\u0000\u0000\u0000\u0215\u0219"+
		"\u0001\u0000\u0000\u0000\u0216\u0214\u0001\u0000\u0000\u0000\u0217\u0218"+
		"\u0005\f\u0000\u0000\u0218\u021a\u0003H$\u0000\u0219\u0217\u0001\u0000"+
		"\u0000\u0000\u0219\u021a\u0001\u0000\u0000\u0000\u021a\u021c\u0001\u0000"+
		"\u0000\u0000\u021b\u020e\u0001\u0000\u0000\u0000\u021b\u020f\u0001\u0000"+
		"\u0000\u0000\u021cA\u0001\u0000\u0000\u0000\u021d\u0222\u0003J%\u0000"+
		"\u021e\u021f\u0005\f\u0000\u0000\u021f\u0221\u0003J%\u0000\u0220\u021e"+
		"\u0001\u0000\u0000\u0000\u0221\u0224\u0001\u0000\u0000\u0000\u0222\u0220"+
		"\u0001\u0000\u0000\u0000\u0222\u0223\u0001\u0000\u0000\u0000\u0223C\u0001"+
		"\u0000\u0000\u0000\u0224\u0222\u0001\u0000\u0000\u0000\u0225\u0228\u0003"+
		"J%\u0000\u0226\u0228\u0003F#\u0000\u0227\u0225\u0001\u0000\u0000\u0000"+
		"\u0227\u0226\u0001\u0000\u0000\u0000\u0228E\u0001\u0000\u0000\u0000\u0229"+
		"\u022b\u0003n7\u0000\u022a\u0229\u0001\u0000\u0000\u0000\u022a\u022b\u0001"+
		"\u0000\u0000\u0000\u022b\u022d\u0001\u0000\u0000\u0000\u022c\u022e\u0003"+
		"L&\u0000\u022d\u022c\u0001\u0000\u0000\u0000\u022d\u022e\u0001\u0000\u0000"+
		"\u0000\u022e\u022f\u0001\u0000\u0000\u0000\u022f\u0238\u0003N\'\u0000"+
		"\u0230\u0232\u0005\u000e\u0000\u0000\u0231\u0233\u0003<\u001e\u0000\u0232"+
		"\u0231\u0001\u0000\u0000\u0000\u0232\u0233\u0001\u0000\u0000\u0000\u0233"+
		"\u0239\u0001\u0000\u0000\u0000\u0234\u0236\u0003<\u001e\u0000\u0235\u0234"+
		"\u0001\u0000\u0000\u0000\u0235\u0236\u0001\u0000\u0000\u0000\u0236\u0237"+
		"\u0001\u0000\u0000\u0000\u0237\u0239\u0003\u0000\u0000\u0000\u0238\u0230"+
		"\u0001\u0000\u0000\u0000\u0238\u0235\u0001\u0000\u0000\u0000\u0239G\u0001"+
		"\u0000\u0000\u0000\u023a\u023b\u0005\u0010\u0000\u0000\u023b\u023d\u0003"+
		"\u00fe\u007f\u0000\u023c\u023e\u0003<\u001e\u0000\u023d\u023c\u0001\u0000"+
		"\u0000\u0000\u023d\u023e\u0001\u0000\u0000\u0000\u023eI\u0001\u0000\u0000"+
		"\u0000\u023f\u0241\u0003n7\u0000\u0240\u023f\u0001\u0000\u0000\u0000\u0240"+
		"\u0241\u0001\u0000\u0000\u0000\u0241\u0243\u0001\u0000\u0000\u0000\u0242"+
		"\u0244\u0003L&\u0000\u0243\u0242\u0001\u0000\u0000\u0000\u0243\u0244\u0001"+
		"\u0000\u0000\u0000\u0244\u0245\u0001\u0000\u0000\u0000\u0245\u0247\u0003"+
		"N\'\u0000\u0246\u0248\u0003<\u001e\u0000\u0247\u0246\u0001\u0000\u0000"+
		"\u0000\u0247\u0248\u0001\u0000\u0000\u0000\u0248K\u0001\u0000\u0000\u0000"+
		"\u0249\u024a\u0007\u0002\u0000\u0000\u024aM\u0001\u0000\u0000\u0000\u024b"+
		"\u024e\u0003\u0112\u0089\u0000\u024c\u024e\u0003\u0002\u0001\u0000\u024d"+
		"\u024b\u0001\u0000\u0000\u0000\u024d\u024c\u0001\u0000\u0000\u0000\u024e"+
		"O\u0001\u0000\u0000\u0000\u024f\u0251\u0005D\u0000\u0000\u0250\u0252\u0003"+
		"\u0004\u0002\u0000\u0251\u0250\u0001\u0000\u0000\u0000\u0251\u0252\u0001"+
		"\u0000\u0000\u0000\u0252\u0253\u0001\u0000\u0000\u0000\u0253\u0255\u0005"+
		"\u0006\u0000\u0000\u0254\u0256\u0003@ \u0000\u0255\u0254\u0001\u0000\u0000"+
		"\u0000\u0255\u0256\u0001\u0000\u0000\u0000\u0256\u0257\u0001\u0000\u0000"+
		"\u0000\u0257\u0259\u0005\u0007\u0000\u0000\u0258\u025a\u0003<\u001e\u0000"+
		"\u0259\u0258\u0001\u0000\u0000\u0000\u0259\u025a\u0001\u0000\u0000\u0000"+
		"\u025aQ\u0001\u0000\u0000\u0000\u025b\u025c\u0005\u0004\u0000\u0000\u025c"+
		"\u025d\u0005|\u0000\u0000\u025d\u025e\u0005\u000f\u0000\u0000\u025e\u025f"+
		"\u0007\u0003\u0000\u0000\u025f\u0260\u0005\u0005\u0000\u0000\u0260\u0261"+
		"\u0003<\u001e\u0000\u0261S\u0001\u0000\u0000\u0000\u0262\u0264\u0003\u00f2"+
		"y\u0000\u0263\u0265\u0005\u000e\u0000\u0000\u0264\u0263\u0001\u0000\u0000"+
		"\u0000\u0264\u0265\u0001\u0000\u0000\u0000\u0265\u0266\u0001\u0000\u0000"+
		"\u0000\u0266\u0267\u0003>\u001f\u0000\u0267U\u0001\u0000\u0000\u0000\u0268"+
		"\u0269\u0005q\u0000\u0000\u0269\u026b\u0005|\u0000\u0000\u026a\u026c\u0003"+
		"\u0004\u0002\u0000\u026b\u026a\u0001\u0000\u0000\u0000\u026b\u026c\u0001"+
		"\u0000\u0000\u0000\u026c\u026d\u0001\u0000\u0000\u0000\u026d\u026e\u0005"+
		"\r\u0000\u0000\u026e\u026f\u0003\u0012\t\u0000\u026f\u0270\u0005\u000b"+
		"\u0000\u0000\u0270W\u0001\u0000\u0000\u0000\u0271\u0273\u0003L&\u0000"+
		"\u0272\u0271\u0001\u0000\u0000\u0000\u0272\u0273\u0001\u0000\u0000\u0000"+
		"\u0273\u0274\u0001\u0000\u0000\u0000\u0274\u0275\u0005t\u0000\u0000\u0275"+
		"\u0277\u0005\u0006\u0000\u0000\u0276\u0278\u0003\u00dam\u0000\u0277\u0276"+
		"\u0001\u0000\u0000\u0000\u0277\u0278\u0001\u0000\u0000\u0000\u0278\u0279"+
		"\u0001\u0000\u0000\u0000\u0279\u027f\u0005\u0007\u0000\u0000\u027a\u027b"+
		"\u0005\b\u0000\u0000\u027b\u027c\u0003\u00e0p\u0000\u027c\u027d\u0005"+
		"\n\u0000\u0000\u027d\u0280\u0001\u0000\u0000\u0000\u027e\u0280\u0005\u000b"+
		"\u0000\u0000\u027f\u027a\u0001\u0000\u0000\u0000\u027f\u027e\u0001\u0000"+
		"\u0000\u0000\u027f\u0280\u0001\u0000\u0000\u0000\u0280Y\u0001\u0000\u0000"+
		"\u0000\u0281\u0283\u0005a\u0000\u0000\u0282\u0281\u0001\u0000\u0000\u0000"+
		"\u0282\u0283\u0001\u0000\u0000\u0000\u0283\u0285\u0001\u0000\u0000\u0000"+
		"\u0284\u0286\u0005x\u0000\u0000\u0285\u0284\u0001\u0000\u0000\u0000\u0285"+
		"\u0286\u0001\u0000\u0000\u0000\u0286\u0287\u0001\u0000\u0000\u0000\u0287"+
		"\u0288\u0005g\u0000\u0000\u0288\u028a\u0005|\u0000\u0000\u0289\u028b\u0003"+
		"\u0004\u0002\u0000\u028a\u0289\u0001\u0000\u0000\u0000\u028a\u028b\u0001"+
		"\u0000\u0000\u0000\u028b\u028d\u0001\u0000\u0000\u0000\u028c\u028e\u0003"+
		"\\.\u0000\u028d\u028c\u0001\u0000\u0000\u0000\u028d\u028e\u0001\u0000"+
		"\u0000\u0000\u028e\u028f\u0001\u0000\u0000\u0000\u028f\u0291\u0003$\u0012"+
		"\u0000\u0290\u0292\u0005\u000b\u0000\u0000\u0291\u0290\u0001\u0000\u0000"+
		"\u0000\u0291\u0292\u0001\u0000\u0000\u0000\u0292[\u0001\u0000\u0000\u0000"+
		"\u0293\u0294\u0005^\u0000\u0000\u0294\u0295\u0003^/\u0000\u0295]\u0001"+
		"\u0000\u0000\u0000\u0296\u029b\u0003\u001a\r\u0000\u0297\u0298\u0005\f"+
		"\u0000\u0000\u0298\u029a\u0003\u001a\r\u0000\u0299\u0297\u0001\u0000\u0000"+
		"\u0000\u029a\u029d\u0001\u0000\u0000\u0000\u029b\u0299\u0001\u0000\u0000"+
		"\u0000\u029b\u029c\u0001\u0000\u0000\u0000\u029c_\u0001\u0000\u0000\u0000"+
		"\u029d\u029b\u0001\u0000\u0000\u0000\u029e\u02a0\u0005`\u0000\u0000\u029f"+
		"\u029e\u0001\u0000\u0000\u0000\u029f\u02a0\u0001\u0000\u0000\u0000\u02a0"+
		"\u02a1\u0001\u0000\u0000\u0000\u02a1\u02a2\u0005]\u0000\u0000\u02a2\u02a3"+
		"\u0005|\u0000\u0000\u02a3\u02a5\u0005\b\u0000\u0000\u02a4\u02a6\u0003"+
		"b1\u0000\u02a5\u02a4\u0001\u0000\u0000\u0000\u02a5\u02a6\u0001\u0000\u0000"+
		"\u0000\u02a6\u02a7\u0001\u0000\u0000\u0000\u02a7\u02a8\u0005\n\u0000\u0000"+
		"\u02a8a\u0001\u0000\u0000\u0000\u02a9\u02ab\u0003d2\u0000\u02aa\u02ac"+
		"\u0005\f\u0000\u0000\u02ab\u02aa\u0001\u0000\u0000\u0000\u02ab\u02ac\u0001"+
		"\u0000\u0000\u0000\u02acc\u0001\u0000\u0000\u0000\u02ad\u02b2\u0003f3"+
		"\u0000\u02ae\u02af\u0005\f\u0000\u0000\u02af\u02b1\u0003f3\u0000\u02b0"+
		"\u02ae\u0001\u0000\u0000\u0000\u02b1\u02b4\u0001\u0000\u0000\u0000\u02b2"+
		"\u02b0\u0001\u0000\u0000\u0000\u02b2\u02b3\u0001\u0000\u0000\u0000\u02b3"+
		"e\u0001\u0000\u0000\u0000\u02b4\u02b2\u0001\u0000\u0000\u0000\u02b5\u02b8"+
		"\u0003\u00f2y\u0000\u02b6\u02b7\u0005\r\u0000\u0000\u02b7\u02b9\u0003"+
		"\u00fe\u007f\u0000\u02b8\u02b6\u0001\u0000\u0000\u0000\u02b8\u02b9\u0001"+
		"\u0000\u0000\u0000\u02b9g\u0001\u0000\u0000\u0000\u02ba\u02bb\u0005u\u0000"+
		"\u0000\u02bb\u02bc\u0003j5\u0000\u02bc\u02be\u0005\b\u0000\u0000\u02bd"+
		"\u02bf\u0003~?\u0000\u02be\u02bd\u0001\u0000\u0000\u0000\u02be\u02bf\u0001"+
		"\u0000\u0000\u0000\u02bf\u02c0\u0001\u0000\u0000\u0000\u02c0\u02c1\u0005"+
		"\n\u0000\u0000\u02c1i\u0001\u0000\u0000\u0000\u02c2\u02cb\u0005|\u0000"+
		"\u0000\u02c3\u02c5\u0005\u0011\u0000\u0000\u02c4\u02c3\u0001\u0000\u0000"+
		"\u0000\u02c5\u02c6\u0001\u0000\u0000\u0000\u02c6\u02c4\u0001\u0000\u0000"+
		"\u0000\u02c6\u02c7\u0001\u0000\u0000\u0000\u02c7\u02c8\u0001\u0000\u0000"+
		"\u0000\u02c8\u02ca\u0005|\u0000\u0000\u02c9\u02c4\u0001\u0000\u0000\u0000"+
		"\u02ca\u02cd\u0001\u0000\u0000\u0000\u02cb\u02c9\u0001\u0000\u0000\u0000"+
		"\u02cb\u02cc\u0001\u0000\u0000\u0000\u02cck\u0001\u0000\u0000\u0000\u02cd"+
		"\u02cb\u0001\u0000\u0000\u0000\u02ce\u02cf\u0005|\u0000\u0000\u02cf\u02d0"+
		"\u0005\r\u0000\u0000\u02d0\u02d1\u0003j5\u0000\u02d1\u02d2\u0005\u000b"+
		"\u0000\u0000\u02d2m\u0001\u0000\u0000\u0000\u02d3\u02d5\u0003p8\u0000"+
		"\u02d4\u02d3\u0001\u0000\u0000\u0000\u02d5\u02d6\u0001\u0000\u0000\u0000"+
		"\u02d6\u02d4\u0001\u0000\u0000\u0000\u02d6\u02d7\u0001\u0000\u0000\u0000"+
		"\u02d7o\u0001\u0000\u0000\u0000\u02d8\u02db\u0005{\u0000\u0000\u02d9\u02dc"+
		"\u0003r9\u0000\u02da\u02dc\u0003t:\u0000\u02db\u02d9\u0001\u0000\u0000"+
		"\u0000\u02db\u02da\u0001\u0000\u0000\u0000\u02dcq\u0001\u0000\u0000\u0000"+
		"\u02dd\u02de\u00069\uffff\uffff\u0000\u02de\u02e4\u0005|\u0000\u0000\u02df"+
		"\u02e0\u0005\u0006\u0000\u0000\u02e0\u02e1\u0003\u00fe\u007f\u0000\u02e1"+
		"\u02e2\u0005\u0007\u0000\u0000\u02e2\u02e4\u0001\u0000\u0000\u0000\u02e3"+
		"\u02dd\u0001\u0000\u0000\u0000\u02e3\u02df\u0001\u0000\u0000\u0000\u02e4"+
		"\u02ea\u0001\u0000\u0000\u0000\u02e5\u02e6\n\u0002\u0000\u0000\u02e6\u02e7"+
		"\u0005\u0011\u0000\u0000\u02e7\u02e9\u0003\u0112\u0089\u0000\u02e8\u02e5"+
		"\u0001\u0000\u0000\u0000\u02e9\u02ec\u0001\u0000\u0000\u0000\u02ea\u02e8"+
		"\u0001\u0000\u0000\u0000\u02ea\u02eb\u0001\u0000\u0000\u0000\u02ebs\u0001"+
		"\u0000\u0000\u0000\u02ec\u02ea\u0001\u0000\u0000\u0000\u02ed\u02ee\u0003"+
		"r9\u0000\u02ee\u02ef\u0003\u00f4z\u0000\u02efu\u0001\u0000\u0000\u0000"+
		"\u02f0\u02f2\u0003\u00e2q\u0000\u02f1\u02f0\u0001\u0000\u0000\u0000\u02f1"+
		"\u02f2\u0001\u0000\u0000\u0000\u02f2\u02f3\u0001\u0000\u0000\u0000\u02f3"+
		"\u02f4\u0005\u0000\u0000\u0001\u02f4w\u0001\u0000\u0000\u0000\u02f5\u02f7"+
		"\u0005a\u0000\u0000\u02f6\u02f5\u0001\u0000\u0000\u0000\u02f6\u02f7\u0001"+
		"\u0000\u0000\u0000\u02f7\u02f8\u0001\u0000\u0000\u0000\u02f8\u02f9\u0003"+
		"z=\u0000\u02f9y\u0001\u0000\u0000\u0000\u02fa\u0318\u0003|>\u0000\u02fb"+
		"\u0318\u0003\u0082A\u0000\u02fc\u0318\u0003\u0088D\u0000\u02fd\u0318\u0003"+
		"\u0090H\u0000\u02fe\u0318\u0003\u0080@\u0000\u02ff\u0318\u0003\u00bc^"+
		"\u0000\u0300\u0318\u0003Z-\u0000\u0301\u0318\u0003h4\u0000\u0302\u0318"+
		"\u0003\u0094J\u0000\u0303\u0318\u0003\u0096K\u0000\u0304\u0318\u0003\u009a"+
		"M\u0000\u0305\u0318\u0003\u009cN\u0000\u0306\u0318\u0003\u009eO\u0000"+
		"\u0307\u0318\u0003\u00a0P\u0000\u0308\u0318\u0003\u00a2Q\u0000\u0309\u0318"+
		"\u0003\u00aeW\u0000\u030a\u0318\u0003\u00a4R\u0000\u030b\u0318\u0003\u00b0"+
		"X\u0000\u030c\u0318\u0003\u00b2Y\u0000\u030d\u0318\u0003\u00b8\\\u0000"+
		"\u030e\u0318\u0003\u00ba]\u0000\u030f\u0318\u0003\u0102\u0081\u0000\u0310"+
		"\u0318\u0003\u00d0h\u0000\u0311\u0318\u0003\u008aE\u0000\u0312\u0318\u0003"+
		"V+\u0000\u0313\u0318\u0003`0\u0000\u0314\u0318\u0003\u0092I\u0000\u0315"+
		"\u0316\u0005a\u0000\u0000\u0316\u0318\u0003z=\u0000\u0317\u02fa\u0001"+
		"\u0000\u0000\u0000\u0317\u02fb\u0001\u0000\u0000\u0000\u0317\u02fc\u0001"+
		"\u0000\u0000\u0000\u0317\u02fd\u0001\u0000\u0000\u0000\u0317\u02fe\u0001"+
		"\u0000\u0000\u0000\u0317\u02ff\u0001\u0000\u0000\u0000\u0317\u0300\u0001"+
		"\u0000\u0000\u0000\u0317\u0301\u0001\u0000\u0000\u0000\u0317\u0302\u0001"+
		"\u0000\u0000\u0000\u0317\u0303\u0001\u0000\u0000\u0000\u0317\u0304\u0001"+
		"\u0000\u0000\u0000\u0317\u0305\u0001\u0000\u0000\u0000\u0317\u0306\u0001"+
		"\u0000\u0000\u0000\u0317\u0307\u0001\u0000\u0000\u0000\u0317\u0308\u0001"+
		"\u0000\u0000\u0000\u0317\u0309\u0001\u0000\u0000\u0000\u0317\u030a\u0001"+
		"\u0000\u0000\u0000\u0317\u030b\u0001\u0000\u0000\u0000\u0317\u030c\u0001"+
		"\u0000\u0000\u0000\u0317\u030d\u0001\u0000\u0000\u0000\u0317\u030e\u0001"+
		"\u0000\u0000\u0000\u0317\u030f\u0001\u0000\u0000\u0000\u0317\u0310\u0001"+
		"\u0000\u0000\u0000\u0317\u0311\u0001\u0000\u0000\u0000\u0317\u0312\u0001"+
		"\u0000\u0000\u0000\u0317\u0313\u0001\u0000\u0000\u0000\u0317\u0314\u0001"+
		"\u0000\u0000\u0000\u0317\u0315\u0001\u0000\u0000\u0000\u0318{\u0001\u0000"+
		"\u0000\u0000\u0319\u031b\u0005\b\u0000\u0000\u031a\u031c\u0003~?\u0000"+
		"\u031b\u031a\u0001\u0000\u0000\u0000\u031b\u031c\u0001\u0000\u0000\u0000"+
		"\u031c\u031d\u0001\u0000\u0000\u0000\u031d\u031e\u0005\n\u0000\u0000\u031e"+
		"}\u0001\u0000\u0000\u0000\u031f\u0321\u0003z=\u0000\u0320\u031f\u0001"+
		"\u0000\u0000\u0000\u0321\u0322\u0001\u0000\u0000\u0000\u0322\u0320\u0001"+
		"\u0000\u0000\u0000\u0322\u0323\u0001\u0000\u0000\u0000\u0323\u007f\u0001"+
		"\u0000\u0000\u0000\u0324\u0328\u0005y\u0000\u0000\u0325\u0326\u0005|\u0000"+
		"\u0000\u0326\u0329\u0003>\u001f\u0000\u0327\u0329\u0003\u008aE\u0000\u0328"+
		"\u0325\u0001\u0000\u0000\u0000\u0328\u0327\u0001\u0000\u0000\u0000\u0329"+
		"\u032a\u0001\u0000\u0000\u0000\u032a\u032b\u0003\u011e\u008f\u0000\u032b"+
		"\u0081\u0001\u0000\u0000\u0000\u032c\u032f\u0005b\u0000\u0000\u032d\u0330"+
		"\u0003\u0084B\u0000\u032e\u0330\u0003l6\u0000\u032f\u032d\u0001\u0000"+
		"\u0000\u0000\u032f\u032e\u0001\u0000\u0000\u0000\u0330\u0083\u0001\u0000"+
		"\u0000\u0000\u0331\u0334\u0005\u0018\u0000\u0000\u0332\u0334\u0003\u0086"+
		"C\u0000\u0333\u0331\u0001\u0000\u0000\u0000\u0333\u0332\u0001\u0000\u0000"+
		"\u0000\u0334\u0337\u0001\u0000\u0000\u0000\u0335\u0336\u0005X\u0000\u0000"+
		"\u0336\u0338\u0003\u0112\u0089\u0000\u0337\u0335\u0001\u0000\u0000\u0000"+
		"\u0337\u0338\u0001\u0000\u0000\u0000\u0338\u0339\u0001\u0000\u0000\u0000"+
		"\u0339\u033a\u0005Y\u0000\u0000\u033a\u033b\u0005}\u0000\u0000\u033b\u033c"+
		"\u0003\u011e\u008f\u0000\u033c\u0085\u0001\u0000\u0000\u0000\u033d\u033e"+
		"\u0003\u0112\u0089\u0000\u033e\u033f\u0005\f\u0000\u0000\u033f\u0341\u0001"+
		"\u0000\u0000\u0000\u0340\u033d\u0001\u0000\u0000\u0000\u0340\u0341\u0001"+
		"\u0000\u0000\u0000\u0341\u0342\u0001\u0000\u0000\u0000\u0342\u0343\u0005"+
		"\b\u0000\u0000\u0343\u0348\u0003\u0112\u0089\u0000\u0344\u0345\u0005\f"+
		"\u0000\u0000\u0345\u0347\u0003\u0112\u0089\u0000\u0346\u0344\u0001\u0000"+
		"\u0000\u0000\u0347\u034a\u0001\u0000\u0000\u0000\u0348\u0346\u0001\u0000"+
		"\u0000\u0000\u0348\u0349\u0001\u0000\u0000\u0000\u0349\u034b\u0001\u0000"+
		"\u0000\u0000\u034a\u0348\u0001\u0000\u0000\u0000\u034b\u034c\u0005\n\u0000"+
		"\u0000\u034c\u0087\u0001\u0000\u0000\u0000\u034d\u034f\u0005a\u0000\u0000"+
		"\u034e\u0350\u0005R\u0000\u0000\u034f\u034e\u0001\u0000\u0000\u0000\u034f"+
		"\u0350\u0001\u0000\u0000\u0000\u0350\u0353\u0001\u0000\u0000\u0000\u0351"+
		"\u0354\u0003\u0084B\u0000\u0352\u0354\u0003z=\u0000\u0353\u0351\u0001"+
		"\u0000\u0000\u0000\u0353\u0352\u0001\u0000\u0000\u0000\u0354\u0089\u0001"+
		"\u0000\u0000\u0000\u0355\u0357\u0003\u0002\u0001\u0000\u0356\u0358\u0003"+
		"<\u001e\u0000\u0357\u0356\u0001\u0000\u0000\u0000\u0357\u0358\u0001\u0000"+
		"\u0000\u0000\u0358\u0359\u0001\u0000\u0000\u0000\u0359\u035b\u0003\u0000"+
		"\u0000\u0000\u035a\u035c\u0005\u000b\u0000\u0000\u035b\u035a\u0001\u0000"+
		"\u0000\u0000\u035b\u035c\u0001\u0000\u0000\u0000\u035c\u0373\u0001\u0000"+
		"\u0000\u0000\u035d\u035f\u0003L&\u0000\u035e\u035d\u0001\u0000\u0000\u0000"+
		"\u035e\u035f\u0001\u0000\u0000\u0000\u035f\u0361\u0001\u0000\u0000\u0000"+
		"\u0360\u0362\u0003\u0098L\u0000\u0361\u0360\u0001\u0000\u0000\u0000\u0361"+
		"\u0362\u0001\u0000\u0000\u0000\u0362\u0364\u0001\u0000\u0000\u0000\u0363"+
		"\u0365\u0005Z\u0000\u0000\u0364\u0363\u0001\u0000\u0000\u0000\u0364\u0365"+
		"\u0001\u0000\u0000\u0000\u0365\u0366\u0001\u0000\u0000\u0000\u0366\u0368"+
		"\u0003\u008cF\u0000\u0367\u0369\u0005\u000b\u0000\u0000\u0368\u0367\u0001"+
		"\u0000\u0000\u0000\u0368\u0369\u0001\u0000\u0000\u0000\u0369\u0373\u0001"+
		"\u0000\u0000\u0000\u036a\u036c\u0005x\u0000\u0000\u036b\u036d\u0003\u0098"+
		"L\u0000\u036c\u036b\u0001\u0000\u0000\u0000\u036c\u036d\u0001\u0000\u0000"+
		"\u0000\u036d\u036e\u0001\u0000\u0000\u0000\u036e\u0370\u0003\u008cF\u0000"+
		"\u036f\u0371\u0005\u000b\u0000\u0000\u0370\u036f\u0001\u0000\u0000\u0000"+
		"\u0370\u0371\u0001\u0000\u0000\u0000\u0371\u0373\u0001\u0000\u0000\u0000"+
		"\u0372\u0355\u0001\u0000\u0000\u0000\u0372\u035e\u0001\u0000\u0000\u0000"+
		"\u0372\u036a\u0001\u0000\u0000\u0000\u0373\u008b\u0001\u0000\u0000\u0000"+
		"\u0374\u0379\u0003\u008eG\u0000\u0375\u0376\u0005\f\u0000\u0000\u0376"+
		"\u0378\u0003\u008eG\u0000\u0377\u0375\u0001\u0000\u0000\u0000\u0378\u037b"+
		"\u0001\u0000\u0000\u0000\u0379\u0377\u0001\u0000\u0000\u0000\u0379\u037a"+
		"\u0001\u0000\u0000\u0000\u037a\u008d\u0001\u0000\u0000\u0000\u037b\u0379"+
		"\u0001\u0000\u0000\u0000\u037c\u0380\u0003\u0114\u008a\u0000\u037d\u0380"+
		"\u0003\u00e4r\u0000\u037e\u0380\u0003\u00eau\u0000\u037f\u037c\u0001\u0000"+
		"\u0000\u0000\u037f\u037d\u0001\u0000\u0000\u0000\u037f\u037e\u0001\u0000"+
		"\u0000\u0000\u0380\u0382\u0001\u0000\u0000\u0000\u0381\u0383\u0003<\u001e"+
		"\u0000\u0382\u0381\u0001\u0000\u0000\u0000\u0382\u0383\u0001\u0000\u0000"+
		"\u0000\u0383\u0385\u0001\u0000\u0000\u0000\u0384\u0386\u0003\u00fe\u007f"+
		"\u0000\u0385\u0384\u0001\u0000\u0000\u0000\u0385\u0386\u0001\u0000\u0000"+
		"\u0000\u0386\u038c\u0001\u0000\u0000\u0000\u0387\u0389\u0005\r\u0000\u0000"+
		"\u0388\u038a\u0003\u0004\u0002\u0000\u0389\u0388\u0001\u0000\u0000\u0000"+
		"\u0389\u038a\u0001\u0000\u0000\u0000\u038a\u038b\u0001\u0000\u0000\u0000"+
		"\u038b\u038d\u0003\u00fe\u007f\u0000\u038c\u0387\u0001\u0000\u0000\u0000"+
		"\u038c\u038d\u0001\u0000\u0000\u0000\u038d\u008f\u0001\u0000\u0000\u0000"+
		"\u038e\u038f\u0005\u000b\u0000\u0000\u038f\u0091\u0001\u0000\u0000\u0000"+
		"\u0390\u0391\u0004I\u0006\u0000\u0391\u0393\u0003\u00fa}\u0000\u0392\u0394"+
		"\u0005\u000b\u0000\u0000\u0393\u0392\u0001\u0000\u0000\u0000\u0393\u0394"+
		"\u0001\u0000\u0000\u0000\u0394\u0093\u0001\u0000\u0000\u0000\u0395\u0396"+
		"\u0005S\u0000\u0000\u0396\u0397\u0005\u0006\u0000\u0000\u0397\u0398\u0003"+
		"\u00fa}\u0000\u0398\u0399\u0005\u0007\u0000\u0000\u0399\u039c\u0003z="+
		"\u0000\u039a\u039b\u0005C\u0000\u0000\u039b\u039d\u0003z=\u0000\u039c"+
		"\u039a\u0001\u0000\u0000\u0000\u039c\u039d\u0001\u0000\u0000\u0000\u039d"+
		"\u0095\u0001\u0000\u0000\u0000\u039e\u039f\u0005?\u0000\u0000\u039f\u03a0"+
		"\u0003z=\u0000\u03a0\u03a1\u0005M\u0000\u0000\u03a1\u03a2\u0005\u0006"+
		"\u0000\u0000\u03a2\u03a3\u0003\u00fa}\u0000\u03a3\u03a4\u0005\u0007\u0000"+
		"\u0000\u03a4\u03a5\u0003\u011e\u008f\u0000\u03a5\u03e4\u0001\u0000\u0000"+
		"\u0000\u03a6\u03a7\u0005M\u0000\u0000\u03a7\u03a8\u0005\u0006\u0000\u0000"+
		"\u03a8\u03a9\u0003\u00fa}\u0000\u03a9\u03aa\u0005\u0007\u0000\u0000\u03aa"+
		"\u03ab\u0003z=\u0000\u03ab\u03e4\u0001\u0000\u0000\u0000\u03ac\u03ad\u0005"+
		"K\u0000\u0000\u03ad\u03af\u0005\u0006\u0000\u0000\u03ae\u03b0\u0003\u00fa"+
		"}\u0000\u03af\u03ae\u0001\u0000\u0000\u0000\u03af\u03b0\u0001\u0000\u0000"+
		"\u0000\u03b0\u03b1\u0001\u0000\u0000\u0000\u03b1\u03b3\u0005\u000b\u0000"+
		"\u0000\u03b2\u03b4\u0003\u00fa}\u0000\u03b3\u03b2\u0001\u0000\u0000\u0000"+
		"\u03b3\u03b4\u0001\u0000\u0000\u0000\u03b4\u03b5\u0001\u0000\u0000\u0000"+
		"\u03b5\u03b7\u0005\u000b\u0000\u0000\u03b6\u03b8\u0003\u00fa}\u0000\u03b7"+
		"\u03b6\u0001\u0000\u0000\u0000\u03b7\u03b8\u0001\u0000\u0000\u0000\u03b8"+
		"\u03b9\u0001\u0000\u0000\u0000\u03b9\u03ba\u0005\u0007\u0000\u0000\u03ba"+
		"\u03e4\u0003z=\u0000\u03bb\u03bc\u0005K\u0000\u0000\u03bc\u03bd\u0005"+
		"\u0006\u0000\u0000\u03bd\u03be\u0003\u0098L\u0000\u03be\u03bf\u0003\u008c"+
		"F\u0000\u03bf\u03c1\u0005\u000b\u0000\u0000\u03c0\u03c2\u0003\u00fa}\u0000"+
		"\u03c1\u03c0\u0001\u0000\u0000\u0000\u03c1\u03c2\u0001\u0000\u0000\u0000"+
		"\u03c2\u03c3\u0001\u0000\u0000\u0000\u03c3\u03c5\u0005\u000b\u0000\u0000"+
		"\u03c4\u03c6\u0003\u00fa}\u0000\u03c5\u03c4\u0001\u0000\u0000\u0000\u03c5"+
		"\u03c6\u0001\u0000\u0000\u0000\u03c6\u03c7\u0001\u0000\u0000\u0000\u03c7"+
		"\u03c8\u0005\u0007\u0000\u0000\u03c8\u03c9\u0003z=\u0000\u03c9\u03e4\u0001"+
		"\u0000\u0000\u0000\u03ca\u03cb\u0005K\u0000\u0000\u03cb\u03cc\u0005\u0006"+
		"\u0000\u0000\u03cc\u03d0\u0003\u00fe\u007f\u0000\u03cd\u03d1\u0005V\u0000"+
		"\u0000\u03ce\u03cf\u0005|\u0000\u0000\u03cf\u03d1\u0004K\u0007\u0000\u03d0"+
		"\u03cd\u0001\u0000\u0000\u0000\u03d0\u03ce\u0001\u0000\u0000\u0000\u03d1"+
		"\u03d2\u0001\u0000\u0000\u0000\u03d2\u03d3\u0003\u00fa}\u0000\u03d3\u03d4"+
		"\u0005\u0007\u0000\u0000\u03d4\u03d5\u0003z=\u0000\u03d5\u03e4\u0001\u0000"+
		"\u0000\u0000\u03d6\u03d7\u0005K\u0000\u0000\u03d7\u03d8\u0005\u0006\u0000"+
		"\u0000\u03d8\u03d9\u0003\u0098L\u0000\u03d9\u03dd\u0003\u008eG\u0000\u03da"+
		"\u03de\u0005V\u0000\u0000\u03db\u03dc\u0005|\u0000\u0000\u03dc\u03de\u0004"+
		"K\b\u0000\u03dd\u03da\u0001\u0000\u0000\u0000\u03dd\u03db\u0001\u0000"+
		"\u0000\u0000\u03de\u03df\u0001\u0000\u0000\u0000\u03df\u03e0\u0003\u00fa"+
		"}\u0000\u03e0\u03e1\u0005\u0007\u0000\u0000\u03e1\u03e2\u0003z=\u0000"+
		"\u03e2\u03e4\u0001\u0000\u0000\u0000\u03e3\u039e\u0001\u0000\u0000\u0000"+
		"\u03e3\u03a6\u0001\u0000\u0000\u0000\u03e3\u03ac\u0001\u0000\u0000\u0000"+
		"\u03e3\u03bb\u0001\u0000\u0000\u0000\u03e3\u03ca\u0001\u0000\u0000\u0000"+
		"\u03e3\u03d6\u0001\u0000\u0000\u0000\u03e4\u0097\u0001\u0000\u0000\u0000"+
		"\u03e5\u03e6\u0007\u0004\u0000\u0000\u03e6\u0099\u0001\u0000\u0000\u0000"+
		"\u03e7\u03ea\u0005J\u0000\u0000\u03e8\u03e9\u0004M\t\u0000\u03e9\u03eb"+
		"\u0005|\u0000\u0000\u03ea\u03e8\u0001\u0000\u0000\u0000\u03ea\u03eb\u0001"+
		"\u0000\u0000\u0000\u03eb\u03ec\u0001\u0000\u0000\u0000\u03ec\u03ed\u0003"+
		"\u011e\u008f\u0000\u03ed\u009b\u0001\u0000\u0000\u0000\u03ee\u03f1\u0005"+
		">\u0000\u0000\u03ef\u03f0\u0004N\n\u0000\u03f0\u03f2\u0005|\u0000\u0000"+
		"\u03f1\u03ef\u0001\u0000\u0000\u0000\u03f1\u03f2\u0001\u0000\u0000\u0000"+
		"\u03f2\u03f3\u0001\u0000\u0000\u0000\u03f3\u03f4\u0003\u011e\u008f\u0000"+
		"\u03f4\u009d\u0001\u0000\u0000\u0000\u03f5\u03f8\u0005H\u0000\u0000\u03f6"+
		"\u03f7\u0004O\u000b\u0000\u03f7\u03f9\u0003\u00fa}\u0000\u03f8\u03f6\u0001"+
		"\u0000\u0000\u0000\u03f8\u03f9\u0001\u0000\u0000\u0000\u03f9\u03fa\u0001"+
		"\u0000\u0000\u0000\u03fa\u03fb\u0003\u011e\u008f\u0000\u03fb\u009f\u0001"+
		"\u0000\u0000\u0000\u03fc\u03ff\u0005k\u0000\u0000\u03fd\u03fe\u0004P\f"+
		"\u0000\u03fe\u0400\u0003\u00fa}\u0000\u03ff\u03fd\u0001\u0000\u0000\u0000"+
		"\u03ff\u0400\u0001\u0000\u0000\u0000\u0400\u0401\u0001\u0000\u0000\u0000"+
		"\u0401\u0402\u0003\u011e\u008f\u0000\u0402\u00a1\u0001\u0000\u0000\u0000"+
		"\u0403\u0404\u0005Q\u0000\u0000\u0404\u0405\u0005\u0006\u0000\u0000\u0405"+
		"\u0406\u0003\u00fa}\u0000\u0406\u0407\u0005\u0007\u0000\u0000\u0407\u0408"+
		"\u0003z=\u0000\u0408\u00a3\u0001\u0000\u0000\u0000\u0409\u040a\u0005L"+
		"\u0000\u0000\u040a\u040b\u0005\u0006\u0000\u0000\u040b\u040c\u0003\u00fa"+
		"}\u0000\u040c\u040d\u0005\u0007\u0000\u0000\u040d\u040e\u0003\u00a6S\u0000"+
		"\u040e\u00a5\u0001\u0000\u0000\u0000\u040f\u0411\u0005\b\u0000\u0000\u0410"+
		"\u0412\u0003\u00a8T\u0000\u0411\u0410\u0001\u0000\u0000\u0000\u0411\u0412"+
		"\u0001\u0000\u0000\u0000\u0412\u0417\u0001\u0000\u0000\u0000\u0413\u0415"+
		"\u0003\u00acV\u0000\u0414\u0416\u0003\u00a8T\u0000\u0415\u0414\u0001\u0000"+
		"\u0000\u0000\u0415\u0416\u0001\u0000\u0000\u0000\u0416\u0418\u0001\u0000"+
		"\u0000\u0000\u0417\u0413\u0001\u0000\u0000\u0000\u0417\u0418\u0001\u0000"+
		"\u0000\u0000\u0418\u0419\u0001\u0000\u0000\u0000\u0419\u041a\u0005\n\u0000"+
		"\u0000\u041a\u00a7\u0001\u0000\u0000\u0000\u041b\u041d\u0003\u00aaU\u0000"+
		"\u041c\u041b\u0001\u0000\u0000\u0000\u041d\u041e\u0001\u0000\u0000\u0000"+
		"\u041e\u041c\u0001\u0000\u0000\u0000\u041e\u041f\u0001\u0000\u0000\u0000"+
		"\u041f\u00a9\u0001\u0000\u0000\u0000\u0420\u0421\u0005B\u0000\u0000\u0421"+
		"\u0422\u0003\u00fa}\u0000\u0422\u0424\u0005\u000f\u0000\u0000\u0423\u0425"+
		"\u0003~?\u0000\u0424\u0423\u0001\u0000\u0000\u0000\u0424\u0425\u0001\u0000"+
		"\u0000\u0000\u0425\u00ab\u0001\u0000\u0000\u0000\u0426\u0427\u0005R\u0000"+
		"\u0000\u0427\u0429\u0005\u000f\u0000\u0000\u0428\u042a\u0003~?\u0000\u0429"+
		"\u0428\u0001\u0000\u0000\u0000\u0429\u042a\u0001\u0000\u0000\u0000\u042a"+
		"\u00ad\u0001\u0000\u0000\u0000\u042b\u042c\u0005|\u0000\u0000\u042c\u042d"+
		"\u0005\u000f\u0000\u0000\u042d\u042e\u0003z=\u0000\u042e\u00af\u0001\u0000"+
		"\u0000\u0000\u042f\u0430\u0005T\u0000\u0000\u0430\u0431\u0004X\r\u0000"+
		"\u0431\u0432\u0003\u00fa}\u0000\u0432\u0433\u0003\u011e\u008f\u0000\u0433"+
		"\u00b1\u0001\u0000\u0000\u0000\u0434\u0435\u0005W\u0000\u0000\u0435\u043b"+
		"\u0003|>\u0000\u0436\u0438\u0003\u00b4Z\u0000\u0437\u0439\u0003\u00b6"+
		"[\u0000\u0438\u0437\u0001\u0000\u0000\u0000\u0438\u0439\u0001\u0000\u0000"+
		"\u0000\u0439\u043c\u0001\u0000\u0000\u0000\u043a\u043c\u0003\u00b6[\u0000"+
		"\u043b\u0436\u0001\u0000\u0000\u0000\u043b\u043a\u0001\u0000\u0000\u0000"+
		"\u043c\u00b3\u0001\u0000\u0000\u0000\u043d\u043e\u0005F\u0000\u0000\u043e"+
		"\u043f\u0005\u0006\u0000\u0000\u043f\u0440\u0005|\u0000\u0000\u0440\u0441"+
		"\u0005\u0007\u0000\u0000\u0441\u0442\u0003|>\u0000\u0442\u00b5\u0001\u0000"+
		"\u0000\u0000\u0443\u0444\u0005G\u0000\u0000\u0444\u0445\u0003|>\u0000"+
		"\u0445\u00b7\u0001\u0000\u0000\u0000\u0446\u0447\u0005N\u0000\u0000\u0447"+
		"\u0448\u0003\u011e\u008f\u0000\u0448\u00b9\u0001\u0000\u0000\u0000\u0449"+
		"\u044a\u0005O\u0000\u0000\u044a\u044b\u0005|\u0000\u0000\u044b\u0451\u0003"+
		">\u001f\u0000\u044c\u044d\u0005\b\u0000\u0000\u044d\u044e\u0003\u00e0"+
		"p\u0000\u044e\u044f\u0005\n\u0000\u0000\u044f\u0452\u0001\u0000\u0000"+
		"\u0000\u0450\u0452\u0005\u000b\u0000\u0000\u0451\u044c\u0001\u0000\u0000"+
		"\u0000\u0451\u0450\u0001\u0000\u0000\u0000\u0452\u00bb\u0001\u0000\u0000"+
		"\u0000\u0453\u0455\u0003n7\u0000\u0454\u0453\u0001\u0000\u0000\u0000\u0454"+
		"\u0455\u0001\u0000\u0000\u0000\u0455\u045a\u0001\u0000\u0000\u0000\u0456"+
		"\u0458\u0005a\u0000\u0000\u0457\u0459\u0005R\u0000\u0000\u0458\u0457\u0001"+
		"\u0000\u0000\u0000\u0458\u0459\u0001\u0000\u0000\u0000\u0459\u045b\u0001"+
		"\u0000\u0000\u0000\u045a\u0456\u0001\u0000\u0000\u0000\u045a\u045b\u0001"+
		"\u0000\u0000\u0000\u045b\u045d\u0001\u0000\u0000\u0000\u045c\u045e\u0005"+
		"y\u0000\u0000\u045d\u045c\u0001\u0000\u0000\u0000\u045d\u045e\u0001\u0000"+
		"\u0000\u0000\u045e\u045f\u0001\u0000\u0000\u0000\u045f\u0460\u0005\\\u0000"+
		"\u0000\u0460\u0462\u0005|\u0000\u0000\u0461\u0463\u0003\u0004\u0002\u0000"+
		"\u0462\u0461\u0001\u0000\u0000\u0000\u0462\u0463\u0001\u0000\u0000\u0000"+
		"\u0463\u0464\u0001\u0000\u0000\u0000\u0464\u0465\u0003\u00be_\u0000\u0465"+
		"\u0466\u0003\u00c0`\u0000\u0466\u00bd\u0001\u0000\u0000\u0000\u0467\u0469"+
		"\u0003\u00c2a\u0000\u0468\u0467\u0001\u0000\u0000\u0000\u0468\u0469\u0001"+
		"\u0000\u0000\u0000\u0469\u046b\u0001\u0000\u0000\u0000\u046a\u046c\u0003"+
		"\u00c4b\u0000\u046b\u046a\u0001\u0000\u0000\u0000\u046b\u046c\u0001\u0000"+
		"\u0000\u0000\u046c\u00bf\u0001\u0000\u0000\u0000\u046d\u0471\u0005\b\u0000"+
		"\u0000\u046e\u0470\u0003\u00c6c\u0000\u046f\u046e\u0001\u0000\u0000\u0000"+
		"\u0470\u0473\u0001\u0000\u0000\u0000\u0471\u046f\u0001\u0000\u0000\u0000"+
		"\u0471\u0472\u0001\u0000\u0000\u0000\u0472\u0474\u0001\u0000\u0000\u0000"+
		"\u0473\u0471\u0001\u0000\u0000\u0000\u0474\u0475\u0005\n\u0000\u0000\u0475"+
		"\u00c1\u0001\u0000\u0000\u0000\u0476\u0477\u0005^\u0000\u0000\u0477\u0478"+
		"\u0003\u001a\r\u0000\u0478\u00c3\u0001\u0000\u0000\u0000\u0479\u047a\u0005"+
		"c\u0000\u0000\u047a\u047b\u0003^/\u0000\u047b\u00c5\u0001\u0000\u0000"+
		"\u0000\u047c\u0484\u0003X,\u0000\u047d\u047f\u0003n7\u0000\u047e\u047d"+
		"\u0001\u0000\u0000\u0000\u047e\u047f\u0001\u0000\u0000\u0000\u047f\u0480"+
		"\u0001\u0000\u0000\u0000\u0480\u0484\u0003\u00c8d\u0000\u0481\u0484\u0003"+
		"\u00ccf\u0000\u0482\u0484\u0003z=\u0000\u0483\u047c\u0001\u0000\u0000"+
		"\u0000\u0483\u047e\u0001\u0000\u0000\u0000\u0483\u0481\u0001\u0000\u0000"+
		"\u0000\u0483\u0482\u0001\u0000\u0000\u0000\u0484\u00c7\u0001\u0000\u0000"+
		"\u0000\u0485\u0486\u0003\u00cae\u0000\u0486\u0488\u0003\u00f2y\u0000\u0487"+
		"\u0489\u0005\u000e\u0000\u0000\u0488\u0487\u0001\u0000\u0000\u0000\u0488"+
		"\u0489\u0001\u0000\u0000\u0000\u0489\u048b\u0001\u0000\u0000\u0000\u048a"+
		"\u048c\u0003<\u001e\u0000\u048b\u048a\u0001\u0000\u0000\u0000\u048b\u048c"+
		"\u0001\u0000\u0000\u0000\u048c\u048e\u0001\u0000\u0000\u0000\u048d\u048f"+
		"\u0003\u0000\u0000\u0000\u048e\u048d\u0001\u0000\u0000\u0000\u048e\u048f"+
		"\u0001\u0000\u0000\u0000\u048f\u0490\u0001\u0000\u0000\u0000\u0490\u0491"+
		"\u0005\u000b\u0000\u0000\u0491\u04a3\u0001\u0000\u0000\u0000\u0492\u0493"+
		"\u0003\u00cae\u0000\u0493\u0494\u0003\u00f2y\u0000\u0494\u049a\u0003>"+
		"\u001f\u0000\u0495\u0496\u0005\b\u0000\u0000\u0496\u0497\u0003\u00e0p"+
		"\u0000\u0497\u0498\u0005\n\u0000\u0000\u0498\u049b\u0001\u0000\u0000\u0000"+
		"\u0499\u049b\u0005\u000b\u0000\u0000\u049a\u0495\u0001\u0000\u0000\u0000"+
		"\u049a\u0499\u0001\u0000\u0000\u0000\u049b\u04a3\u0001\u0000\u0000\u0000"+
		"\u049c\u049f\u0003\u00cae\u0000\u049d\u04a0\u0003\u00eew\u0000\u049e\u04a0"+
		"\u0003\u00f0x\u0000\u049f\u049d\u0001\u0000\u0000\u0000\u049f\u049e\u0001"+
		"\u0000\u0000\u0000\u04a0\u04a3\u0001\u0000\u0000\u0000\u04a1\u04a3\u0003"+
		"\u0080@\u0000\u04a2\u0485\u0001\u0000\u0000\u0000\u04a2\u0492\u0001\u0000"+
		"\u0000\u0000\u04a2\u049c\u0001\u0000\u0000\u0000\u04a2\u04a1\u0001\u0000"+
		"\u0000\u0000\u04a3\u00c9\u0001\u0000\u0000\u0000\u04a4\u04a6\u0003L&\u0000"+
		"\u04a5\u04a4\u0001\u0000\u0000\u0000\u04a5\u04a6\u0001\u0000\u0000\u0000"+
		"\u04a6\u04a8\u0001\u0000\u0000\u0000\u04a7\u04a9\u0005[\u0000\u0000\u04a8"+
		"\u04a7\u0001\u0000\u0000\u0000\u04a8\u04a9\u0001\u0000\u0000\u0000\u04a9"+
		"\u04ab\u0001\u0000\u0000\u0000\u04aa\u04ac\u0005j\u0000\u0000\u04ab\u04aa"+
		"\u0001\u0000\u0000\u0000\u04ab\u04ac\u0001\u0000\u0000\u0000\u04ac\u04ae"+
		"\u0001\u0000\u0000\u0000\u04ad\u04af\u0005Z\u0000\u0000\u04ae\u04ad\u0001"+
		"\u0000\u0000\u0000\u04ae\u04af\u0001\u0000\u0000\u0000\u04af\u00cb\u0001"+
		"\u0000\u0000\u0000\u04b0\u04b1\u0003R)\u0000\u04b1\u04b2\u0005\u000b\u0000"+
		"\u0000\u04b2\u00cd\u0001\u0000\u0000\u0000\u04b3\u04b5\u0005\u0018\u0000"+
		"\u0000\u04b4\u04b3\u0001\u0000\u0000\u0000\u04b4\u04b5\u0001\u0000\u0000"+
		"\u0000\u04b5\u04b6\u0001\u0000\u0000\u0000\u04b6\u04b7\u0005|\u0000\u0000"+
		"\u04b7\u04b9\u0005\u0006\u0000\u0000\u04b8\u04ba\u0003\u00dam\u0000\u04b9"+
		"\u04b8\u0001\u0000\u0000\u0000\u04b9\u04ba\u0001\u0000\u0000\u0000\u04ba"+
		"\u04bb\u0001\u0000\u0000\u0000\u04bb\u04bc\u0005\u0007\u0000\u0000\u04bc"+
		"\u04bd\u0005\b\u0000\u0000\u04bd\u04be\u0003\u00e0p\u0000\u04be\u04bf"+
		"\u0005\n\u0000\u0000\u04bf\u00cf\u0001\u0000\u0000\u0000\u04c0\u04c1\u0005"+
		"O\u0000\u0000\u04c1\u04c3\u0005\u0018\u0000\u0000\u04c2\u04c4\u0005|\u0000"+
		"\u0000\u04c3\u04c2\u0001\u0000\u0000\u0000\u04c3\u04c4\u0001\u0000\u0000"+
		"\u0000\u04c4\u04c5\u0001\u0000\u0000\u0000\u04c5\u04c7\u0005\u0006\u0000"+
		"\u0000\u04c6\u04c8\u0003\u00dam\u0000\u04c7\u04c6\u0001\u0000\u0000\u0000"+
		"\u04c7\u04c8\u0001\u0000\u0000\u0000\u04c8\u04c9\u0001\u0000\u0000\u0000"+
		"\u04c9\u04ca\u0005\u0007\u0000\u0000\u04ca\u04cb\u0005\b\u0000\u0000\u04cb"+
		"\u04cc\u0003\u00e0p\u0000\u04cc\u04cd\u0005\n\u0000\u0000\u04cd\u00d1"+
		"\u0001\u0000\u0000\u0000\u04ce\u04cf\u0005\b\u0000\u0000\u04cf\u04d4\u0003"+
		"\u00d4j\u0000\u04d0\u04d1\u0005\f\u0000\u0000\u04d1\u04d3\u0003\u00d4"+
		"j\u0000\u04d2\u04d0\u0001\u0000\u0000\u0000\u04d3\u04d6\u0001\u0000\u0000"+
		"\u0000\u04d4\u04d2\u0001\u0000\u0000\u0000\u04d4\u04d5\u0001\u0000\u0000"+
		"\u0000\u04d5\u04d8\u0001\u0000\u0000\u0000\u04d6\u04d4\u0001\u0000\u0000"+
		"\u0000\u04d7\u04d9\u0005\f\u0000\u0000\u04d8\u04d7\u0001\u0000\u0000\u0000"+
		"\u04d8\u04d9\u0001\u0000\u0000\u0000\u04d9\u04da\u0001\u0000\u0000\u0000"+
		"\u04da\u04db\u0005\n\u0000\u0000\u04db\u00d3\u0001\u0000\u0000\u0000\u04dc"+
		"\u04dd\u0005\u0018\u0000\u0000\u04dd\u04de\u0003\u00d8l\u0000\u04de\u00d5"+
		"\u0001\u0000\u0000\u0000\u04df\u04e0\u0005\b\u0000\u0000\u04e0\u04e5\u0003"+
		"\u00d8l\u0000\u04e1\u04e2\u0005\f\u0000\u0000\u04e2\u04e4\u0003\u00d8"+
		"l\u0000\u04e3\u04e1\u0001\u0000\u0000\u0000\u04e4\u04e7\u0001\u0000\u0000"+
		"\u0000\u04e5\u04e3\u0001\u0000\u0000\u0000\u04e5\u04e6\u0001\u0000\u0000"+
		"\u0000\u04e6\u04e9\u0001\u0000\u0000\u0000\u04e7\u04e5\u0001\u0000\u0000"+
		"\u0000\u04e8\u04ea\u0005\f\u0000\u0000\u04e9\u04e8\u0001\u0000\u0000\u0000"+
		"\u04e9\u04ea\u0001\u0000\u0000\u0000\u04ea\u04eb\u0001\u0000\u0000\u0000"+
		"\u04eb\u04ec\u0005\n\u0000\u0000\u04ec\u00d7\u0001\u0000\u0000\u0000\u04ed"+
		"\u04ee\u0005\u0004\u0000\u0000\u04ee\u04ef\u0003\u00fe\u007f\u0000\u04ef"+
		"\u04f0\u0005\u0005\u0000\u0000\u04f0\u04f2\u0005\u0006\u0000\u0000\u04f1"+
		"\u04f3\u0003\u00dam\u0000\u04f2\u04f1\u0001\u0000\u0000\u0000\u04f2\u04f3"+
		"\u0001\u0000\u0000\u0000\u04f3\u04f4\u0001\u0000\u0000\u0000\u04f4\u04f5"+
		"\u0005\u0007\u0000\u0000\u04f5\u04f6\u0005\b\u0000\u0000\u04f6\u04f7\u0003"+
		"\u00e0p\u0000\u04f7\u04f8\u0005\n\u0000\u0000\u04f8\u00d9\u0001\u0000"+
		"\u0000\u0000\u04f9\u04fe\u0003\u00dcn\u0000\u04fa\u04fb\u0005\f\u0000"+
		"\u0000\u04fb\u04fd\u0003\u00dcn\u0000\u04fc\u04fa\u0001\u0000\u0000\u0000"+
		"\u04fd\u0500\u0001\u0000\u0000\u0000\u04fe\u04fc\u0001\u0000\u0000\u0000"+
		"\u04fe\u04ff\u0001\u0000\u0000\u0000\u04ff\u0503\u0001\u0000\u0000\u0000"+
		"\u0500\u04fe\u0001\u0000\u0000\u0000\u0501\u0502\u0005\f\u0000\u0000\u0502"+
		"\u0504\u0003\u00deo\u0000\u0503\u0501\u0001\u0000\u0000\u0000\u0503\u0504"+
		"\u0001\u0000\u0000\u0000\u0504\u050d\u0001\u0000\u0000\u0000\u0505\u050d"+
		"\u0003\u00deo\u0000\u0506\u050d\u0003\u00e4r\u0000\u0507\u050a\u0003\u00ea"+
		"u\u0000\u0508\u0509\u0005\u000f\u0000\u0000\u0509\u050b\u0003\u00dam\u0000"+
		"\u050a\u0508\u0001\u0000\u0000\u0000\u050a\u050b\u0001\u0000\u0000\u0000"+
		"\u050b\u050d\u0001\u0000\u0000\u0000\u050c\u04f9\u0001\u0000\u0000\u0000"+
		"\u050c\u0505\u0001\u0000\u0000\u0000\u050c\u0506\u0001\u0000\u0000\u0000"+
		"\u050c\u0507\u0001\u0000\u0000\u0000\u050d\u00db\u0001\u0000\u0000\u0000"+
		"\u050e\u0510\u0003p8\u0000\u050f\u050e\u0001\u0000\u0000\u0000\u050f\u0510"+
		"\u0001\u0000\u0000\u0000\u0510\u0512\u0001\u0000\u0000\u0000\u0511\u0513"+
		"\u0003L&\u0000\u0512\u0511\u0001\u0000\u0000\u0000\u0512\u0513\u0001\u0000"+
		"\u0000\u0000\u0513\u0514\u0001\u0000\u0000\u0000\u0514\u0516\u0003\u0114"+
		"\u008a\u0000\u0515\u0517\u0005\u000e\u0000\u0000\u0516\u0515\u0001\u0000"+
		"\u0000\u0000\u0516\u0517\u0001\u0000\u0000\u0000\u0517\u0519\u0001\u0000"+
		"\u0000\u0000\u0518\u051a\u0003<\u001e\u0000\u0519\u0518\u0001\u0000\u0000"+
		"\u0000\u0519\u051a\u0001\u0000\u0000\u0000\u051a\u051d\u0001\u0000\u0000"+
		"\u0000\u051b\u051c\u0005\r\u0000\u0000\u051c\u051e\u0003\u00fe\u007f\u0000"+
		"\u051d\u051b\u0001\u0000\u0000\u0000\u051d\u051e\u0001\u0000\u0000\u0000"+
		"\u051e\u00dd\u0001\u0000\u0000\u0000\u051f\u0520\u0005\u0010\u0000\u0000"+
		"\u0520\u0522\u0005|\u0000\u0000\u0521\u0523\u0003<\u001e\u0000\u0522\u0521"+
		"\u0001\u0000\u0000\u0000\u0522\u0523\u0001\u0000\u0000\u0000\u0523\u00df"+
		"\u0001\u0000\u0000\u0000\u0524\u0526\u0003\u00e2q\u0000\u0525\u0524\u0001"+
		"\u0000\u0000\u0000\u0525\u0526\u0001\u0000\u0000\u0000\u0526\u00e1\u0001"+
		"\u0000\u0000\u0000\u0527\u0529\u0003x<\u0000\u0528\u0527\u0001\u0000\u0000"+
		"\u0000\u0529\u052a\u0001\u0000\u0000\u0000\u052a\u0528\u0001\u0000\u0000"+
		"\u0000\u052a\u052b\u0001\u0000\u0000\u0000\u052b\u00e3\u0001\u0000\u0000"+
		"\u0000\u052c\u052e\u0005\u0004\u0000\u0000\u052d\u052f\u0003\u00e6s\u0000"+
		"\u052e\u052d\u0001\u0000\u0000\u0000\u052e\u052f\u0001\u0000\u0000\u0000"+
		"\u052f\u0530\u0001\u0000\u0000\u0000\u0530\u0531\u0005\u0005\u0000\u0000"+
		"\u0531\u00e5\u0001\u0000\u0000\u0000\u0532\u053b\u0003\u00e8t\u0000\u0533"+
		"\u0535\u0005\f\u0000\u0000\u0534\u0533\u0001\u0000\u0000\u0000\u0535\u0536"+
		"\u0001\u0000\u0000\u0000\u0536\u0534\u0001\u0000\u0000\u0000\u0536\u0537"+
		"\u0001\u0000\u0000\u0000\u0537\u0538\u0001\u0000\u0000\u0000\u0538\u053a"+
		"\u0003\u00e8t\u0000\u0539\u0534\u0001\u0000\u0000\u0000\u053a\u053d\u0001"+
		"\u0000\u0000\u0000\u053b\u0539\u0001\u0000\u0000\u0000\u053b\u053c\u0001"+
		"\u0000\u0000\u0000\u053c\u00e7\u0001\u0000\u0000\u0000\u053d\u053b\u0001"+
		"\u0000\u0000\u0000\u053e\u0540\u0005\u0010\u0000\u0000\u053f\u053e\u0001"+
		"\u0000\u0000\u0000\u053f\u0540\u0001\u0000\u0000\u0000\u0540\u0543\u0001"+
		"\u0000\u0000\u0000\u0541\u0544\u0003\u00fe\u007f\u0000\u0542\u0544\u0005"+
		"|\u0000\u0000\u0543\u0541\u0001\u0000\u0000\u0000\u0543\u0542\u0001\u0000"+
		"\u0000\u0000\u0544\u0546\u0001\u0000\u0000\u0000\u0545\u0547\u0005\f\u0000"+
		"\u0000\u0546\u0545\u0001\u0000\u0000\u0000\u0546\u0547\u0001\u0000\u0000"+
		"\u0000\u0547\u00e9\u0001\u0000\u0000\u0000\u0548\u0554\u0005\b\u0000\u0000"+
		"\u0549\u054e\u0003\u00ecv\u0000\u054a\u054b\u0005\f\u0000\u0000\u054b"+
		"\u054d\u0003\u00ecv\u0000\u054c\u054a\u0001\u0000\u0000\u0000\u054d\u0550"+
		"\u0001\u0000\u0000\u0000\u054e\u054c\u0001\u0000\u0000\u0000\u054e\u054f"+
		"\u0001\u0000\u0000\u0000\u054f\u0552\u0001\u0000\u0000\u0000\u0550\u054e"+
		"\u0001\u0000\u0000\u0000\u0551\u0553\u0005\f\u0000\u0000\u0552\u0551\u0001"+
		"\u0000\u0000\u0000\u0552\u0553\u0001\u0000\u0000\u0000\u0553\u0555\u0001"+
		"\u0000\u0000\u0000\u0554\u0549\u0001\u0000\u0000\u0000\u0554\u0555\u0001"+
		"\u0000\u0000\u0000\u0555\u0556\u0001\u0000\u0000\u0000\u0556\u0557\u0005"+
		"\n\u0000\u0000\u0557\u00eb\u0001\u0000\u0000\u0000\u0558\u0559\u0003\u00f2"+
		"y\u0000\u0559\u055a\u0007\u0005\u0000\u0000\u055a\u055b\u0003\u00fe\u007f"+
		"\u0000\u055b\u0568\u0001\u0000\u0000\u0000\u055c\u055d\u0005\u0004\u0000"+
		"\u0000\u055d\u055e\u0003\u00fe\u007f\u0000\u055e\u055f\u0005\u0005\u0000"+
		"\u0000\u055f\u0560\u0005\u000f\u0000\u0000\u0560\u0561\u0003\u00fe\u007f"+
		"\u0000\u0561\u0568\u0001\u0000\u0000\u0000\u0562\u0568\u0003\u00eew\u0000"+
		"\u0563\u0568\u0003\u00f0x\u0000\u0564\u0568\u0003\u00ceg\u0000\u0565\u0568"+
		"\u0003\u0114\u008a\u0000\u0566\u0568\u0003H$\u0000\u0567\u0558\u0001\u0000"+
		"\u0000\u0000\u0567\u055c\u0001\u0000\u0000\u0000\u0567\u0562\u0001\u0000"+
		"\u0000\u0000\u0567\u0563\u0001\u0000\u0000\u0000\u0567\u0564\u0001\u0000"+
		"\u0000\u0000\u0567\u0565\u0001\u0000\u0000\u0000\u0567\u0566\u0001\u0000"+
		"\u0000\u0000\u0568\u00ed\u0001\u0000\u0000\u0000\u0569\u056a\u0003\u011a"+
		"\u008d\u0000\u056a\u056b\u0005\u0006\u0000\u0000\u056b\u056d\u0005\u0007"+
		"\u0000\u0000\u056c\u056e\u0003<\u001e\u0000\u056d\u056c\u0001\u0000\u0000"+
		"\u0000\u056d\u056e\u0001\u0000\u0000\u0000\u056e\u056f\u0001\u0000\u0000"+
		"\u0000\u056f\u0570\u0005\b\u0000\u0000\u0570\u0571\u0003\u00e0p\u0000"+
		"\u0571\u0572\u0005\n\u0000\u0000\u0572\u00ef\u0001\u0000\u0000\u0000\u0573"+
		"\u0574\u0003\u011c\u008e\u0000\u0574\u0577\u0005\u0006\u0000\u0000\u0575"+
		"\u0578\u0005|\u0000\u0000\u0576\u0578\u0003\u0002\u0001\u0000\u0577\u0575"+
		"\u0001\u0000\u0000\u0000\u0577\u0576\u0001\u0000\u0000\u0000\u0578\u057a"+
		"\u0001\u0000\u0000\u0000\u0579\u057b\u0003<\u001e\u0000\u057a\u0579\u0001"+
		"\u0000\u0000\u0000\u057a\u057b\u0001\u0000\u0000\u0000\u057b\u057c\u0001"+
		"\u0000\u0000\u0000\u057c\u057d\u0005\u0007\u0000\u0000\u057d\u057e\u0005"+
		"\b\u0000\u0000\u057e\u057f\u0003\u00e0p\u0000\u057f\u0580\u0005\n\u0000"+
		"\u0000\u0580\u00f1\u0001\u0000\u0000\u0000\u0581\u0585\u0003\u0112\u0089"+
		"\u0000\u0582\u0585\u0005}\u0000\u0000\u0583\u0585\u0003\u0110\u0088\u0000"+
		"\u0584\u0581\u0001\u0000\u0000\u0000\u0584\u0582\u0001\u0000\u0000\u0000"+
		"\u0584\u0583\u0001\u0000\u0000\u0000\u0585\u00f3\u0001\u0000\u0000\u0000"+
		"\u0586\u058b\u0005\u0006\u0000\u0000\u0587\u0589\u0003\u00f6{\u0000\u0588"+
		"\u058a\u0005\f\u0000\u0000\u0589\u0588\u0001\u0000\u0000\u0000\u0589\u058a"+
		"\u0001\u0000\u0000\u0000\u058a\u058c\u0001\u0000\u0000\u0000\u058b\u0587"+
		"\u0001\u0000\u0000\u0000\u058b\u058c\u0001\u0000\u0000\u0000\u058c\u058d"+
		"\u0001\u0000\u0000\u0000\u058d\u058e\u0005\u0007\u0000\u0000\u058e\u00f5"+
		"\u0001\u0000\u0000\u0000\u058f\u0594\u0003\u00f8|\u0000\u0590\u0591\u0005"+
		"\f\u0000\u0000\u0591\u0593\u0003\u00f8|\u0000\u0592\u0590\u0001\u0000"+
		"\u0000\u0000\u0593\u0596\u0001\u0000\u0000\u0000\u0594\u0592\u0001\u0000"+
		"\u0000\u0000\u0594\u0595\u0001\u0000\u0000\u0000\u0595\u00f7\u0001\u0000"+
		"\u0000\u0000\u0596\u0594\u0001\u0000\u0000\u0000\u0597\u0599\u0005\u0010"+
		"\u0000\u0000\u0598\u0597\u0001\u0000\u0000\u0000\u0598\u0599\u0001\u0000"+
		"\u0000\u0000\u0599\u059c\u0001\u0000\u0000\u0000\u059a\u059d\u0003\u00fe"+
		"\u007f\u0000\u059b\u059d\u0005|\u0000\u0000\u059c\u059a\u0001\u0000\u0000"+
		"\u0000\u059c\u059b\u0001\u0000\u0000\u0000\u059d\u00f9\u0001\u0000\u0000"+
		"\u0000\u059e\u05a3\u0003\u00fe\u007f\u0000\u059f\u05a0\u0005\f\u0000\u0000"+
		"\u05a0\u05a2\u0003\u00fe\u007f\u0000\u05a1\u059f\u0001\u0000\u0000\u0000"+
		"\u05a2\u05a5\u0001\u0000\u0000\u0000\u05a3\u05a1\u0001\u0000\u0000\u0000"+
		"\u05a3\u05a4\u0001\u0000\u0000\u0000\u05a4\u00fb\u0001\u0000\u0000\u0000"+
		"\u05a5\u05a3\u0001\u0000\u0000\u0000\u05a6\u05a8\u0005O\u0000\u0000\u05a7"+
		"\u05a9\u0005|\u0000\u0000\u05a8\u05a7\u0001\u0000\u0000\u0000\u05a8\u05a9"+
		"\u0001\u0000\u0000\u0000\u05a9\u05aa\u0001\u0000\u0000\u0000\u05aa\u05ac"+
		"\u0005\u0006\u0000\u0000\u05ab\u05ad\u0003\u00dam\u0000\u05ac\u05ab\u0001"+
		"\u0000\u0000\u0000\u05ac\u05ad\u0001\u0000\u0000\u0000\u05ad\u05ae\u0001"+
		"\u0000\u0000\u0000\u05ae\u05b0\u0005\u0007\u0000\u0000\u05af\u05b1\u0003"+
		"<\u001e\u0000\u05b0\u05af\u0001\u0000\u0000\u0000\u05b0\u05b1\u0001\u0000"+
		"\u0000\u0000\u05b1\u05b2\u0001\u0000\u0000\u0000\u05b2\u05b3\u0005\b\u0000"+
		"\u0000\u05b3\u05b4\u0003\u00e0p\u0000\u05b4\u05b5\u0005\n\u0000\u0000"+
		"\u05b5\u00fd\u0001\u0000\u0000\u0000\u05b6\u05b7\u0006\u007f\uffff\uffff"+
		"\u0000\u05b7\u05ed\u0003\u00fc~\u0000\u05b8\u05ed\u0003\u0102\u0081\u0000"+
		"\u05b9\u05ba\u0005D\u0000\u0000\u05ba\u05bc\u0003\u00fe\u007f\u0000\u05bb"+
		"\u05bd\u0003\f\u0006\u0000\u05bc\u05bb\u0001\u0000\u0000\u0000\u05bc\u05bd"+
		"\u0001\u0000\u0000\u0000\u05bd\u05be\u0001\u0000\u0000\u0000\u05be\u05bf"+
		"\u0003\u00f4z\u0000\u05bf\u05ed\u0001\u0000\u0000\u0000\u05c0\u05c1\u0005"+
		"D\u0000\u0000\u05c1\u05c3\u0003\u00fe\u007f\u0000\u05c2\u05c4\u0003\f"+
		"\u0006\u0000\u05c3\u05c2\u0001\u0000\u0000\u0000\u05c3\u05c4\u0001\u0000"+
		"\u0000\u0000\u05c4\u05ed\u0001\u0000\u0000\u0000\u05c5\u05c6\u0005U\u0000"+
		"\u0000\u05c6\u05ed\u0003\u00fe\u007f&\u05c7\u05c8\u0005I\u0000\u0000\u05c8"+
		"\u05ed\u0003\u00fe\u007f%\u05c9\u05ca\u0005A\u0000\u0000\u05ca\u05ed\u0003"+
		"\u00fe\u007f$\u05cb\u05cc\u0005\u0012\u0000\u0000\u05cc\u05ed\u0003\u00fe"+
		"\u007f#\u05cd\u05ce\u0005\u0013\u0000\u0000\u05ce\u05ed\u0003\u00fe\u007f"+
		"\"\u05cf\u05d0\u0005\u0014\u0000\u0000\u05d0\u05ed\u0003\u00fe\u007f!"+
		"\u05d1\u05d2\u0005\u0015\u0000\u0000\u05d2\u05ed\u0003\u00fe\u007f \u05d3"+
		"\u05d4\u0005\u0016\u0000\u0000\u05d4\u05ed\u0003\u00fe\u007f\u001f\u05d5"+
		"\u05d6\u0005\u0017\u0000\u0000\u05d6\u05ed\u0003\u00fe\u007f\u001e\u05d7"+
		"\u05ed\u0003\u00d6k\u0000\u05d8\u05ed\u0003\u00d2i\u0000\u05d9\u05ed\u0003"+
		"\u00d0h\u0000\u05da\u05ed\u0003\u00a0P\u0000\u05db\u05ed\u0005P\u0000"+
		"\u0000\u05dc\u05de\u0003\u0112\u0089\u0000\u05dd\u05df\u0003\u00fe\u007f"+
		"\u0000\u05de\u05dd\u0001\u0000\u0000\u0000\u05de\u05df\u0001\u0000\u0000"+
		"\u0000\u05df\u05ed\u0001\u0000\u0000\u0000\u05e0\u05ed\u0005_\u0000\u0000"+
		"\u05e1\u05ed\u0003\u010a\u0085\u0000\u05e2\u05ed\u0003\u00e4r\u0000\u05e3"+
		"\u05ed\u0003\u00eau\u0000\u05e4\u05e5\u0005\u0006\u0000\u0000\u05e5\u05e6"+
		"\u0003\u00fa}\u0000\u05e6\u05e7\u0005\u0007\u0000\u0000\u05e7\u05ed\u0001"+
		"\u0000\u0000\u0000\u05e8\u05ea\u0003\f\u0006\u0000\u05e9\u05eb\u0003\u00fa"+
		"}\u0000\u05ea\u05e9\u0001\u0000\u0000\u0000\u05ea\u05eb\u0001\u0000\u0000"+
		"\u0000\u05eb\u05ed\u0001\u0000\u0000\u0000\u05ec\u05b6\u0001\u0000\u0000"+
		"\u0000\u05ec\u05b8\u0001\u0000\u0000\u0000\u05ec\u05b9\u0001\u0000\u0000"+
		"\u0000\u05ec\u05c0\u0001\u0000\u0000\u0000\u05ec\u05c5\u0001\u0000\u0000"+
		"\u0000\u05ec\u05c7\u0001\u0000\u0000\u0000\u05ec\u05c9\u0001\u0000\u0000"+
		"\u0000\u05ec\u05cb\u0001\u0000\u0000\u0000\u05ec\u05cd\u0001\u0000\u0000"+
		"\u0000\u05ec\u05cf\u0001\u0000\u0000\u0000\u05ec\u05d1\u0001\u0000\u0000"+
		"\u0000\u05ec\u05d3\u0001\u0000\u0000\u0000\u05ec\u05d5\u0001\u0000\u0000"+
		"\u0000\u05ec\u05d7\u0001\u0000\u0000\u0000\u05ec\u05d8\u0001\u0000\u0000"+
		"\u0000\u05ec\u05d9\u0001\u0000\u0000\u0000\u05ec\u05da\u0001\u0000\u0000"+
		"\u0000\u05ec\u05db\u0001\u0000\u0000\u0000\u05ec\u05dc\u0001\u0000\u0000"+
		"\u0000\u05ec\u05e0\u0001\u0000\u0000\u0000\u05ec\u05e1\u0001\u0000\u0000"+
		"\u0000\u05ec\u05e2\u0001\u0000\u0000\u0000\u05ec\u05e3\u0001\u0000\u0000"+
		"\u0000\u05ec\u05e4\u0001\u0000\u0000\u0000\u05ec\u05e8\u0001\u0000\u0000"+
		"\u0000\u05ed\u063c\u0001\u0000\u0000\u0000\u05ee\u05ef\n\u001d\u0000\u0000"+
		"\u05ef\u05f0\u0007\u0006\u0000\u0000\u05f0\u063b\u0003\u00fe\u007f\u001e"+
		"\u05f1\u05f2\n\u001c\u0000\u0000\u05f2\u05f3\u0007\u0007\u0000\u0000\u05f3"+
		"\u063b\u0003\u00fe\u007f\u001d\u05f4\u05f5\n\u001b\u0000\u0000\u05f5\u05f6"+
		"\u0007\b\u0000\u0000\u05f6\u063b\u0003\u00fe\u007f\u001c\u05f7\u05f8\n"+
		"\u001a\u0000\u0000\u05f8\u05f9\u0007\t\u0000\u0000\u05f9\u063b\u0003\u00fe"+
		"\u007f\u001b\u05fa\u05fb\n\u0019\u0000\u0000\u05fb\u05fc\u0005@\u0000"+
		"\u0000\u05fc\u063b\u0003\u00fe\u007f\u001a\u05fd\u05fe\n\u0018\u0000\u0000"+
		"\u05fe\u05ff\u0005V\u0000\u0000\u05ff\u063b\u0003\u00fe\u007f\u0019\u0600"+
		"\u0601\n\u0017\u0000\u0000\u0601\u0602\u0007\n\u0000\u0000\u0602\u063b"+
		"\u0003\u00fe\u007f\u0018\u0603\u0604\n\u0016\u0000\u0000\u0604\u0605\u0005"+
		"&\u0000\u0000\u0605\u063b\u0003\u00fe\u007f\u0017\u0606\u0607\n\u0015"+
		"\u0000\u0000\u0607\u0608\u0005\'\u0000\u0000\u0608\u063b\u0003\u00fe\u007f"+
		"\u0016\u0609\u060a\n\u0014\u0000\u0000\u060a\u060b\u0005(\u0000\u0000"+
		"\u060b\u063b\u0003\u00fe\u007f\u0015\u060c\u060d\n\u0013\u0000\u0000\u060d"+
		"\u060e\u0005)\u0000\u0000\u060e\u063b\u0003\u00fe\u007f\u0014\u060f\u0610"+
		"\n\u0012\u0000\u0000\u0610\u0611\u0005*\u0000\u0000\u0611\u063b\u0003"+
		"\u00fe\u007f\u0013\u0612\u0613\n\u0011\u0000\u0000\u0613\u0614\u0005\u000e"+
		"\u0000\u0000\u0614\u0615\u0003\u00fe\u007f\u0000\u0615\u0616\u0005\u000f"+
		"\u0000\u0000\u0616\u0617\u0003\u00fe\u007f\u0012\u0617\u063b\u0001\u0000"+
		"\u0000\u0000\u0618\u0619\n\u0010\u0000\u0000\u0619\u061a\u0005\r\u0000"+
		"\u0000\u061a\u063b\u0003\u00fe\u007f\u0011\u061b\u061c\n\u000f\u0000\u0000"+
		"\u061c\u061d\u0003\u0108\u0084\u0000\u061d\u061e\u0003\u00fe\u007f\u0010"+
		"\u061e\u063b\u0001\u0000\u0000\u0000\u061f\u0620\n-\u0000\u0000\u0620"+
		"\u0621\u0005\u0004\u0000\u0000\u0621\u0622\u0003\u00fa}\u0000\u0622\u0623"+
		"\u0005\u0005\u0000\u0000\u0623\u063b\u0001\u0000\u0000\u0000\u0624\u0626"+
		"\n,\u0000\u0000\u0625\u0627\u0005\u0017\u0000\u0000\u0626\u0625\u0001"+
		"\u0000\u0000\u0000\u0626\u0627\u0001\u0000\u0000\u0000\u0627\u0628\u0001"+
		"\u0000\u0000\u0000\u0628\u0629\u0005\u0011\u0000\u0000\u0629\u062b\u0003"+
		"\u0112\u0089\u0000\u062a\u062c\u0003\u001c\u000e\u0000\u062b\u062a\u0001"+
		"\u0000\u0000\u0000\u062b\u062c\u0001\u0000\u0000\u0000\u062c\u063b\u0001"+
		"\u0000\u0000\u0000\u062d\u062e\n)\u0000\u0000\u062e\u063b\u0003\u00f4"+
		"z\u0000\u062f\u0630\n(\u0000\u0000\u0630\u0631\u0004\u007f!\u0000\u0631"+
		"\u063b\u0005\u0012\u0000\u0000\u0632\u0633\n\'\u0000\u0000\u0633\u0634"+
		"\u0004\u007f#\u0000\u0634\u063b\u0005\u0013\u0000\u0000\u0635\u0636\n"+
		"\u000e\u0000\u0000\u0636\u063b\u0003\u010c\u0086\u0000\u0637\u0638\n\u0001"+
		"\u0000\u0000\u0638\u0639\u0005X\u0000\u0000\u0639\u063b\u0003\u0100\u0080"+
		"\u0000\u063a\u05ee\u0001\u0000\u0000\u0000\u063a\u05f1\u0001\u0000\u0000"+
		"\u0000\u063a\u05f4\u0001\u0000\u0000\u0000\u063a\u05f7\u0001\u0000\u0000"+
		"\u0000\u063a\u05fa\u0001\u0000\u0000\u0000\u063a\u05fd\u0001\u0000\u0000"+
		"\u0000\u063a\u0600\u0001\u0000\u0000\u0000\u063a\u0603\u0001\u0000\u0000"+
		"\u0000\u063a\u0606\u0001\u0000\u0000\u0000\u063a\u0609\u0001\u0000\u0000"+
		"\u0000\u063a\u060c\u0001\u0000\u0000\u0000\u063a\u060f\u0001\u0000\u0000"+
		"\u0000\u063a\u0612\u0001\u0000\u0000\u0000\u063a\u0618\u0001\u0000\u0000"+
		"\u0000\u063a\u061b\u0001\u0000\u0000\u0000\u063a\u061f\u0001\u0000\u0000"+
		"\u0000\u063a\u0624\u0001\u0000\u0000\u0000\u063a\u062d\u0001\u0000\u0000"+
		"\u0000\u063a\u062f\u0001\u0000\u0000\u0000\u063a\u0632\u0001\u0000\u0000"+
		"\u0000\u063a\u0635\u0001\u0000\u0000\u0000\u063a\u0637\u0001\u0000\u0000"+
		"\u0000\u063b\u063e\u0001\u0000\u0000\u0000\u063c\u063a\u0001\u0000\u0000"+
		"\u0000\u063c\u063d\u0001\u0000\u0000\u0000\u063d\u00ff\u0001\u0000\u0000"+
		"\u0000\u063e\u063c\u0001\u0000\u0000\u0000\u063f\u0642\u0003\u0018\f\u0000"+
		"\u0640\u0641\u0005\u0004\u0000\u0000\u0641\u0643\u0005\u0005\u0000\u0000"+
		"\u0642\u0640\u0001\u0000\u0000\u0000\u0642\u0643\u0001\u0000\u0000\u0000"+
		"\u0643\u0646\u0001\u0000\u0000\u0000\u0644\u0646\u0003\u00fe\u007f\u0000"+
		"\u0645\u063f\u0001\u0000\u0000\u0000\u0645\u0644\u0001\u0000\u0000\u0000"+
		"\u0646\u0101\u0001\u0000\u0000\u0000\u0647\u0649\u0005[\u0000\u0000\u0648"+
		"\u0647\u0001\u0000\u0000\u0000\u0648\u0649\u0001\u0000\u0000\u0000\u0649"+
		"\u064a\u0001\u0000\u0000\u0000\u064a\u064c\u0003\u0104\u0082\u0000\u064b"+
		"\u064d\u0003<\u001e\u0000\u064c\u064b\u0001\u0000\u0000\u0000\u064c\u064d"+
		"\u0001\u0000\u0000\u0000\u064d\u064e\u0001\u0000\u0000\u0000\u064e\u064f"+
		"\u00056\u0000\u0000\u064f\u0650\u0003\u0106\u0083\u0000\u0650\u0103\u0001"+
		"\u0000\u0000\u0000\u0651\u0658\u0005|\u0000\u0000\u0652\u0654\u0005\u0006"+
		"\u0000\u0000\u0653\u0655\u0003\u00dam\u0000\u0654\u0653\u0001\u0000\u0000"+
		"\u0000\u0654\u0655\u0001\u0000\u0000\u0000\u0655\u0656\u0001\u0000\u0000"+
		"\u0000\u0656\u0658\u0005\u0007\u0000\u0000\u0657\u0651\u0001\u0000\u0000"+
		"\u0000\u0657\u0652\u0001\u0000\u0000\u0000\u0658\u0105\u0001\u0000\u0000"+
		"\u0000\u0659\u065f\u0003\u00fe\u007f\u0000\u065a\u065b\u0005\b\u0000\u0000"+
		"\u065b\u065c\u0003\u00e0p\u0000\u065c\u065d\u0005\n\u0000\u0000\u065d"+
		"\u065f\u0001\u0000\u0000\u0000\u065e\u0659\u0001\u0000\u0000\u0000\u065e"+
		"\u065a\u0001\u0000\u0000\u0000\u065f\u0107\u0001\u0000\u0000\u0000\u0660"+
		"\u0661\u0007\u000b\u0000\u0000\u0661\u0109\u0001\u0000\u0000\u0000\u0662"+
		"\u0669\u00057\u0000\u0000\u0663\u0669\u00058\u0000\u0000\u0664\u0669\u0005"+
		"}\u0000\u0000\u0665\u0669\u0003\u010c\u0086\u0000\u0666\u0669\u0005\u0003"+
		"\u0000\u0000\u0667\u0669\u0003\u0110\u0088\u0000\u0668\u0662\u0001\u0000"+
		"\u0000\u0000\u0668\u0663\u0001\u0000\u0000\u0000\u0668\u0664\u0001\u0000"+
		"\u0000\u0000\u0668\u0665\u0001\u0000\u0000\u0000\u0668\u0666\u0001\u0000"+
		"\u0000\u0000\u0668\u0667\u0001\u0000\u0000\u0000\u0669\u010b\u0001\u0000"+
		"\u0000\u0000\u066a\u066e\u0005~\u0000\u0000\u066b\u066d\u0003\u010e\u0087"+
		"\u0000\u066c\u066b\u0001\u0000\u0000\u0000\u066d\u0670\u0001\u0000\u0000"+
		"\u0000\u066e\u066c\u0001\u0000\u0000\u0000\u066e\u066f\u0001\u0000\u0000"+
		"\u0000\u066f\u0671\u0001\u0000\u0000\u0000\u0670\u066e\u0001\u0000\u0000"+
		"\u0000\u0671\u0672\u0005~\u0000\u0000\u0672\u010d\u0001\u0000\u0000\u0000"+
		"\u0673\u067a\u0005\u0086\u0000\u0000\u0674\u0675\u0005\u0085\u0000\u0000"+
		"\u0675\u0676\u0003\u00fe\u007f\u0000\u0676\u0677\u0005\t\u0000\u0000\u0677"+
		"\u067a\u0001\u0000\u0000\u0000\u0678\u067a\u0005\u0084\u0000\u0000\u0679"+
		"\u0673\u0001\u0000\u0000\u0000\u0679\u0674\u0001\u0000\u0000\u0000\u0679"+
		"\u0678\u0001\u0000\u0000\u0000\u067a\u010f\u0001\u0000\u0000\u0000\u067b"+
		"\u067c\u0007\f\u0000\u0000\u067c\u0111\u0001\u0000\u0000\u0000\u067d\u0680"+
		"\u0005|\u0000\u0000\u067e\u0680\u0003\u0116\u008b\u0000\u067f\u067d\u0001"+
		"\u0000\u0000\u0000\u067f\u067e\u0001\u0000\u0000\u0000\u0680\u0113\u0001"+
		"\u0000\u0000\u0000\u0681\u0682\u0007\r\u0000\u0000\u0682\u0115\u0001\u0000"+
		"\u0000\u0000\u0683\u0687\u0003\u0118\u008c\u0000\u0684\u0687\u00057\u0000"+
		"\u0000\u0685\u0687\u00058\u0000\u0000\u0686\u0683\u0001\u0000\u0000\u0000"+
		"\u0686\u0684\u0001\u0000\u0000\u0000\u0686\u0685\u0001\u0000\u0000\u0000"+
		"\u0687\u0117\u0001\u0000\u0000\u0000\u0688\u0689\u0007\u000e\u0000\u0000"+
		"\u0689\u0119\u0001\u0000\u0000\u0000\u068a\u068b\u0005r\u0000\u0000\u068b"+
		"\u068c\u0003\u00f2y\u0000\u068c\u011b\u0001\u0000\u0000\u0000\u068d\u068e"+
		"\u0005s\u0000\u0000\u068e\u068f\u0003\u00f2y\u0000\u068f\u011d\u0001\u0000"+
		"\u0000\u0000\u0690\u0695\u0005\u000b\u0000\u0000\u0691\u0695\u0005\u0000"+
		"\u0000\u0001\u0692\u0695\u0004\u008f&\u0000\u0693\u0695\u0004\u008f\'"+
		"\u0000\u0694\u0690\u0001\u0000\u0000\u0000\u0694\u0691\u0001\u0000\u0000"+
		"\u0000\u0694\u0692\u0001\u0000\u0000\u0000\u0694\u0693\u0001\u0000\u0000"+
		"\u0000\u0695\u011f\u0001\u0000\u0000\u0000\u00d5\u0125\u0129\u0132\u0137"+
		"\u013a\u0141\u014a\u0154\u015f\u0161\u0176\u017e\u0185\u0189\u0198\u019c"+
		"\u01a0\u01a6\u01ad\u01b7\u01b9\u01c9\u01cd\u01d1\u01d9\u01dd\u01ec\u01f0"+
		"\u01f3\u01f7\u01fa\u01fe\u0204\u0208\u020c\u0214\u0219\u021b\u0222\u0227"+
		"\u022a\u022d\u0232\u0235\u0238\u023d\u0240\u0243\u0247\u024d\u0251\u0255"+
		"\u0259\u0264\u026b\u0272\u0277\u027f\u0282\u0285\u028a\u028d\u0291\u029b"+
		"\u029f\u02a5\u02ab\u02b2\u02b8\u02be\u02c6\u02cb\u02d6\u02db\u02e3\u02ea"+
		"\u02f1\u02f6\u0317\u031b\u0322\u0328\u032f\u0333\u0337\u0340\u0348\u034f"+
		"\u0353\u0357\u035b\u035e\u0361\u0364\u0368\u036c\u0370\u0372\u0379\u037f"+
		"\u0382\u0385\u0389\u038c\u0393\u039c\u03af\u03b3\u03b7\u03c1\u03c5\u03d0"+
		"\u03dd\u03e3\u03ea\u03f1\u03f8\u03ff\u0411\u0415\u0417\u041e\u0424\u0429"+
		"\u0438\u043b\u0451\u0454\u0458\u045a\u045d\u0462\u0468\u046b\u0471\u047e"+
		"\u0483\u0488\u048b\u048e\u049a\u049f\u04a2\u04a5\u04a8\u04ab\u04ae\u04b4"+
		"\u04b9\u04c3\u04c7\u04d4\u04d8\u04e5\u04e9\u04f2\u04fe\u0503\u050a\u050c"+
		"\u050f\u0512\u0516\u0519\u051d\u0522\u0525\u052a\u052e\u0536\u053b\u053f"+
		"\u0543\u0546\u054e\u0552\u0554\u0567\u056d\u0577\u057a\u0584\u0589\u058b"+
		"\u0594\u0598\u059c\u05a3\u05a8\u05ac\u05b0\u05bc\u05c3\u05de\u05ea\u05ec"+
		"\u0626\u062b\u063a\u063c\u0642\u0645\u0648\u064c\u0654\u0657\u065e\u0668"+
		"\u066e\u0679\u067f\u0686\u0694";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}