// Generated from KotlinParser.g4 by ANTLR 4.13.0
package grammar;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link KotlinParser}.
 */
public interface KotlinParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link KotlinParser#kotlinFile}.
	 * @param ctx the parse tree
	 */
	void enterKotlinFile(KotlinParser.KotlinFileContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#kotlinFile}.
	 * @param ctx the parse tree
	 */
	void exitKotlinFile(KotlinParser.KotlinFileContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#script}.
	 * @param ctx the parse tree
	 */
	void enterScript(KotlinParser.ScriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#script}.
	 * @param ctx the parse tree
	 */
	void exitScript(KotlinParser.ScriptContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#preamble}.
	 * @param ctx the parse tree
	 */
	void enterPreamble(KotlinParser.PreambleContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#preamble}.
	 * @param ctx the parse tree
	 */
	void exitPreamble(KotlinParser.PreambleContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#fileAnnotations}.
	 * @param ctx the parse tree
	 */
	void enterFileAnnotations(KotlinParser.FileAnnotationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#fileAnnotations}.
	 * @param ctx the parse tree
	 */
	void exitFileAnnotations(KotlinParser.FileAnnotationsContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#fileAnnotation}.
	 * @param ctx the parse tree
	 */
	void enterFileAnnotation(KotlinParser.FileAnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#fileAnnotation}.
	 * @param ctx the parse tree
	 */
	void exitFileAnnotation(KotlinParser.FileAnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#packageHeader}.
	 * @param ctx the parse tree
	 */
	void enterPackageHeader(KotlinParser.PackageHeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#packageHeader}.
	 * @param ctx the parse tree
	 */
	void exitPackageHeader(KotlinParser.PackageHeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#importList}.
	 * @param ctx the parse tree
	 */
	void enterImportList(KotlinParser.ImportListContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#importList}.
	 * @param ctx the parse tree
	 */
	void exitImportList(KotlinParser.ImportListContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#importHeader}.
	 * @param ctx the parse tree
	 */
	void enterImportHeader(KotlinParser.ImportHeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#importHeader}.
	 * @param ctx the parse tree
	 */
	void exitImportHeader(KotlinParser.ImportHeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#importAlias}.
	 * @param ctx the parse tree
	 */
	void enterImportAlias(KotlinParser.ImportAliasContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#importAlias}.
	 * @param ctx the parse tree
	 */
	void exitImportAlias(KotlinParser.ImportAliasContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#topLevelObject}.
	 * @param ctx the parse tree
	 */
	void enterTopLevelObject(KotlinParser.TopLevelObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#topLevelObject}.
	 * @param ctx the parse tree
	 */
	void exitTopLevelObject(KotlinParser.TopLevelObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(KotlinParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(KotlinParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#primaryConstructor}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryConstructor(KotlinParser.PrimaryConstructorContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#primaryConstructor}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryConstructor(KotlinParser.PrimaryConstructorContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#classParameters}.
	 * @param ctx the parse tree
	 */
	void enterClassParameters(KotlinParser.ClassParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#classParameters}.
	 * @param ctx the parse tree
	 */
	void exitClassParameters(KotlinParser.ClassParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#classParameter}.
	 * @param ctx the parse tree
	 */
	void enterClassParameter(KotlinParser.ClassParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#classParameter}.
	 * @param ctx the parse tree
	 */
	void exitClassParameter(KotlinParser.ClassParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#delegationSpecifiers}.
	 * @param ctx the parse tree
	 */
	void enterDelegationSpecifiers(KotlinParser.DelegationSpecifiersContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#delegationSpecifiers}.
	 * @param ctx the parse tree
	 */
	void exitDelegationSpecifiers(KotlinParser.DelegationSpecifiersContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#delegationSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterDelegationSpecifier(KotlinParser.DelegationSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#delegationSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitDelegationSpecifier(KotlinParser.DelegationSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#constructorInvocation}.
	 * @param ctx the parse tree
	 */
	void enterConstructorInvocation(KotlinParser.ConstructorInvocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#constructorInvocation}.
	 * @param ctx the parse tree
	 */
	void exitConstructorInvocation(KotlinParser.ConstructorInvocationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#explicitDelegation}.
	 * @param ctx the parse tree
	 */
	void enterExplicitDelegation(KotlinParser.ExplicitDelegationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#explicitDelegation}.
	 * @param ctx the parse tree
	 */
	void exitExplicitDelegation(KotlinParser.ExplicitDelegationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(KotlinParser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(KotlinParser.ClassBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#classMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassMemberDeclaration(KotlinParser.ClassMemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#classMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassMemberDeclaration(KotlinParser.ClassMemberDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#anonymousInitializer}.
	 * @param ctx the parse tree
	 */
	void enterAnonymousInitializer(KotlinParser.AnonymousInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#anonymousInitializer}.
	 * @param ctx the parse tree
	 */
	void exitAnonymousInitializer(KotlinParser.AnonymousInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#secondaryConstructor}.
	 * @param ctx the parse tree
	 */
	void enterSecondaryConstructor(KotlinParser.SecondaryConstructorContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#secondaryConstructor}.
	 * @param ctx the parse tree
	 */
	void exitSecondaryConstructor(KotlinParser.SecondaryConstructorContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#constructorDelegationCall}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDelegationCall(KotlinParser.ConstructorDelegationCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#constructorDelegationCall}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDelegationCall(KotlinParser.ConstructorDelegationCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#enumClassBody}.
	 * @param ctx the parse tree
	 */
	void enterEnumClassBody(KotlinParser.EnumClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#enumClassBody}.
	 * @param ctx the parse tree
	 */
	void exitEnumClassBody(KotlinParser.EnumClassBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#enumEntries}.
	 * @param ctx the parse tree
	 */
	void enterEnumEntries(KotlinParser.EnumEntriesContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#enumEntries}.
	 * @param ctx the parse tree
	 */
	void exitEnumEntries(KotlinParser.EnumEntriesContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#enumEntry}.
	 * @param ctx the parse tree
	 */
	void enterEnumEntry(KotlinParser.EnumEntryContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#enumEntry}.
	 * @param ctx the parse tree
	 */
	void exitEnumEntry(KotlinParser.EnumEntryContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(KotlinParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(KotlinParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#functionValueParameters}.
	 * @param ctx the parse tree
	 */
	void enterFunctionValueParameters(KotlinParser.FunctionValueParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#functionValueParameters}.
	 * @param ctx the parse tree
	 */
	void exitFunctionValueParameters(KotlinParser.FunctionValueParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#functionValueParameter}.
	 * @param ctx the parse tree
	 */
	void enterFunctionValueParameter(KotlinParser.FunctionValueParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#functionValueParameter}.
	 * @param ctx the parse tree
	 */
	void exitFunctionValueParameter(KotlinParser.FunctionValueParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(KotlinParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(KotlinParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#receiverType}.
	 * @param ctx the parse tree
	 */
	void enterReceiverType(KotlinParser.ReceiverTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#receiverType}.
	 * @param ctx the parse tree
	 */
	void exitReceiverType(KotlinParser.ReceiverTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void enterFunctionBody(KotlinParser.FunctionBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void exitFunctionBody(KotlinParser.FunctionBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#objectDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterObjectDeclaration(KotlinParser.ObjectDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#objectDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitObjectDeclaration(KotlinParser.ObjectDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#companionObject}.
	 * @param ctx the parse tree
	 */
	void enterCompanionObject(KotlinParser.CompanionObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#companionObject}.
	 * @param ctx the parse tree
	 */
	void exitCompanionObject(KotlinParser.CompanionObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#propertyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPropertyDeclaration(KotlinParser.PropertyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#propertyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPropertyDeclaration(KotlinParser.PropertyDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#multiVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMultiVariableDeclaration(KotlinParser.MultiVariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#multiVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMultiVariableDeclaration(KotlinParser.MultiVariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(KotlinParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(KotlinParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#getter}.
	 * @param ctx the parse tree
	 */
	void enterGetter(KotlinParser.GetterContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#getter}.
	 * @param ctx the parse tree
	 */
	void exitGetter(KotlinParser.GetterContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#setter}.
	 * @param ctx the parse tree
	 */
	void enterSetter(KotlinParser.SetterContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#setter}.
	 * @param ctx the parse tree
	 */
	void exitSetter(KotlinParser.SetterContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#typeAlias}.
	 * @param ctx the parse tree
	 */
	void enterTypeAlias(KotlinParser.TypeAliasContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#typeAlias}.
	 * @param ctx the parse tree
	 */
	void exitTypeAlias(KotlinParser.TypeAliasContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#typeParameters}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameters(KotlinParser.TypeParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#typeParameters}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameters(KotlinParser.TypeParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#typeParameter}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameter(KotlinParser.TypeParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#typeParameter}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameter(KotlinParser.TypeParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(KotlinParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(KotlinParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#typeModifierList}.
	 * @param ctx the parse tree
	 */
	void enterTypeModifierList(KotlinParser.TypeModifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#typeModifierList}.
	 * @param ctx the parse tree
	 */
	void exitTypeModifierList(KotlinParser.TypeModifierListContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#parenthesizedType}.
	 * @param ctx the parse tree
	 */
	void enterParenthesizedType(KotlinParser.ParenthesizedTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#parenthesizedType}.
	 * @param ctx the parse tree
	 */
	void exitParenthesizedType(KotlinParser.ParenthesizedTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#nullableType}.
	 * @param ctx the parse tree
	 */
	void enterNullableType(KotlinParser.NullableTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#nullableType}.
	 * @param ctx the parse tree
	 */
	void exitNullableType(KotlinParser.NullableTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#typeReference}.
	 * @param ctx the parse tree
	 */
	void enterTypeReference(KotlinParser.TypeReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#typeReference}.
	 * @param ctx the parse tree
	 */
	void exitTypeReference(KotlinParser.TypeReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#functionType}.
	 * @param ctx the parse tree
	 */
	void enterFunctionType(KotlinParser.FunctionTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#functionType}.
	 * @param ctx the parse tree
	 */
	void exitFunctionType(KotlinParser.FunctionTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#functionTypeReceiver}.
	 * @param ctx the parse tree
	 */
	void enterFunctionTypeReceiver(KotlinParser.FunctionTypeReceiverContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#functionTypeReceiver}.
	 * @param ctx the parse tree
	 */
	void exitFunctionTypeReceiver(KotlinParser.FunctionTypeReceiverContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#userType}.
	 * @param ctx the parse tree
	 */
	void enterUserType(KotlinParser.UserTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#userType}.
	 * @param ctx the parse tree
	 */
	void exitUserType(KotlinParser.UserTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#simpleUserType}.
	 * @param ctx the parse tree
	 */
	void enterSimpleUserType(KotlinParser.SimpleUserTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#simpleUserType}.
	 * @param ctx the parse tree
	 */
	void exitSimpleUserType(KotlinParser.SimpleUserTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#functionTypeParameters}.
	 * @param ctx the parse tree
	 */
	void enterFunctionTypeParameters(KotlinParser.FunctionTypeParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#functionTypeParameters}.
	 * @param ctx the parse tree
	 */
	void exitFunctionTypeParameters(KotlinParser.FunctionTypeParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#typeConstraints}.
	 * @param ctx the parse tree
	 */
	void enterTypeConstraints(KotlinParser.TypeConstraintsContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#typeConstraints}.
	 * @param ctx the parse tree
	 */
	void exitTypeConstraints(KotlinParser.TypeConstraintsContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#typeConstraint}.
	 * @param ctx the parse tree
	 */
	void enterTypeConstraint(KotlinParser.TypeConstraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#typeConstraint}.
	 * @param ctx the parse tree
	 */
	void exitTypeConstraint(KotlinParser.TypeConstraintContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(KotlinParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(KotlinParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#statements}.
	 * @param ctx the parse tree
	 */
	void enterStatements(KotlinParser.StatementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#statements}.
	 * @param ctx the parse tree
	 */
	void exitStatements(KotlinParser.StatementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(KotlinParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(KotlinParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#blockLevelExpression}.
	 * @param ctx the parse tree
	 */
	void enterBlockLevelExpression(KotlinParser.BlockLevelExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#blockLevelExpression}.
	 * @param ctx the parse tree
	 */
	void exitBlockLevelExpression(KotlinParser.BlockLevelExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(KotlinParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(KotlinParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(KotlinParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(KotlinParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#disjunction}.
	 * @param ctx the parse tree
	 */
	void enterDisjunction(KotlinParser.DisjunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#disjunction}.
	 * @param ctx the parse tree
	 */
	void exitDisjunction(KotlinParser.DisjunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#conjunction}.
	 * @param ctx the parse tree
	 */
	void enterConjunction(KotlinParser.ConjunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#conjunction}.
	 * @param ctx the parse tree
	 */
	void exitConjunction(KotlinParser.ConjunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#equalityComparison}.
	 * @param ctx the parse tree
	 */
	void enterEqualityComparison(KotlinParser.EqualityComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#equalityComparison}.
	 * @param ctx the parse tree
	 */
	void exitEqualityComparison(KotlinParser.EqualityComparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#comparison}.
	 * @param ctx the parse tree
	 */
	void enterComparison(KotlinParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#comparison}.
	 * @param ctx the parse tree
	 */
	void exitComparison(KotlinParser.ComparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#namedInfix}.
	 * @param ctx the parse tree
	 */
	void enterNamedInfix(KotlinParser.NamedInfixContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#namedInfix}.
	 * @param ctx the parse tree
	 */
	void exitNamedInfix(KotlinParser.NamedInfixContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#elvisExpression}.
	 * @param ctx the parse tree
	 */
	void enterElvisExpression(KotlinParser.ElvisExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#elvisExpression}.
	 * @param ctx the parse tree
	 */
	void exitElvisExpression(KotlinParser.ElvisExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#infixFunctionCall}.
	 * @param ctx the parse tree
	 */
	void enterInfixFunctionCall(KotlinParser.InfixFunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#infixFunctionCall}.
	 * @param ctx the parse tree
	 */
	void exitInfixFunctionCall(KotlinParser.InfixFunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#rangeExpression}.
	 * @param ctx the parse tree
	 */
	void enterRangeExpression(KotlinParser.RangeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#rangeExpression}.
	 * @param ctx the parse tree
	 */
	void exitRangeExpression(KotlinParser.RangeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(KotlinParser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(KotlinParser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(KotlinParser.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(KotlinParser.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#typeRHS}.
	 * @param ctx the parse tree
	 */
	void enterTypeRHS(KotlinParser.TypeRHSContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#typeRHS}.
	 * @param ctx the parse tree
	 */
	void exitTypeRHS(KotlinParser.TypeRHSContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#prefixUnaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterPrefixUnaryExpression(KotlinParser.PrefixUnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#prefixUnaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitPrefixUnaryExpression(KotlinParser.PrefixUnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#postfixUnaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostfixUnaryExpression(KotlinParser.PostfixUnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#postfixUnaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostfixUnaryExpression(KotlinParser.PostfixUnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#atomicExpression}.
	 * @param ctx the parse tree
	 */
	void enterAtomicExpression(KotlinParser.AtomicExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#atomicExpression}.
	 * @param ctx the parse tree
	 */
	void exitAtomicExpression(KotlinParser.AtomicExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#parenthesizedExpression}.
	 * @param ctx the parse tree
	 */
	void enterParenthesizedExpression(KotlinParser.ParenthesizedExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#parenthesizedExpression}.
	 * @param ctx the parse tree
	 */
	void exitParenthesizedExpression(KotlinParser.ParenthesizedExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#callSuffix}.
	 * @param ctx the parse tree
	 */
	void enterCallSuffix(KotlinParser.CallSuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#callSuffix}.
	 * @param ctx the parse tree
	 */
	void exitCallSuffix(KotlinParser.CallSuffixContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#annotatedLambda}.
	 * @param ctx the parse tree
	 */
	void enterAnnotatedLambda(KotlinParser.AnnotatedLambdaContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#annotatedLambda}.
	 * @param ctx the parse tree
	 */
	void exitAnnotatedLambda(KotlinParser.AnnotatedLambdaContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#arrayAccess}.
	 * @param ctx the parse tree
	 */
	void enterArrayAccess(KotlinParser.ArrayAccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#arrayAccess}.
	 * @param ctx the parse tree
	 */
	void exitArrayAccess(KotlinParser.ArrayAccessContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#valueArguments}.
	 * @param ctx the parse tree
	 */
	void enterValueArguments(KotlinParser.ValueArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#valueArguments}.
	 * @param ctx the parse tree
	 */
	void exitValueArguments(KotlinParser.ValueArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void enterTypeArguments(KotlinParser.TypeArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void exitTypeArguments(KotlinParser.TypeArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#typeProjection}.
	 * @param ctx the parse tree
	 */
	void enterTypeProjection(KotlinParser.TypeProjectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#typeProjection}.
	 * @param ctx the parse tree
	 */
	void exitTypeProjection(KotlinParser.TypeProjectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#typeProjectionModifierList}.
	 * @param ctx the parse tree
	 */
	void enterTypeProjectionModifierList(KotlinParser.TypeProjectionModifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#typeProjectionModifierList}.
	 * @param ctx the parse tree
	 */
	void exitTypeProjectionModifierList(KotlinParser.TypeProjectionModifierListContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#valueArgument}.
	 * @param ctx the parse tree
	 */
	void enterValueArgument(KotlinParser.ValueArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#valueArgument}.
	 * @param ctx the parse tree
	 */
	void exitValueArgument(KotlinParser.ValueArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#literalConstant}.
	 * @param ctx the parse tree
	 */
	void enterLiteralConstant(KotlinParser.LiteralConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#literalConstant}.
	 * @param ctx the parse tree
	 */
	void exitLiteralConstant(KotlinParser.LiteralConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(KotlinParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(KotlinParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#lineStringLiteral}.
	 * @param ctx the parse tree
	 */
	void enterLineStringLiteral(KotlinParser.LineStringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#lineStringLiteral}.
	 * @param ctx the parse tree
	 */
	void exitLineStringLiteral(KotlinParser.LineStringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#multiLineStringLiteral}.
	 * @param ctx the parse tree
	 */
	void enterMultiLineStringLiteral(KotlinParser.MultiLineStringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#multiLineStringLiteral}.
	 * @param ctx the parse tree
	 */
	void exitMultiLineStringLiteral(KotlinParser.MultiLineStringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#lineStringContent}.
	 * @param ctx the parse tree
	 */
	void enterLineStringContent(KotlinParser.LineStringContentContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#lineStringContent}.
	 * @param ctx the parse tree
	 */
	void exitLineStringContent(KotlinParser.LineStringContentContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#lineStringExpression}.
	 * @param ctx the parse tree
	 */
	void enterLineStringExpression(KotlinParser.LineStringExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#lineStringExpression}.
	 * @param ctx the parse tree
	 */
	void exitLineStringExpression(KotlinParser.LineStringExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#multiLineStringContent}.
	 * @param ctx the parse tree
	 */
	void enterMultiLineStringContent(KotlinParser.MultiLineStringContentContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#multiLineStringContent}.
	 * @param ctx the parse tree
	 */
	void exitMultiLineStringContent(KotlinParser.MultiLineStringContentContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#multiLineStringExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiLineStringExpression(KotlinParser.MultiLineStringExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#multiLineStringExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiLineStringExpression(KotlinParser.MultiLineStringExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#functionLiteral}.
	 * @param ctx the parse tree
	 */
	void enterFunctionLiteral(KotlinParser.FunctionLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#functionLiteral}.
	 * @param ctx the parse tree
	 */
	void exitFunctionLiteral(KotlinParser.FunctionLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#lambdaParameters}.
	 * @param ctx the parse tree
	 */
	void enterLambdaParameters(KotlinParser.LambdaParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#lambdaParameters}.
	 * @param ctx the parse tree
	 */
	void exitLambdaParameters(KotlinParser.LambdaParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#lambdaParameter}.
	 * @param ctx the parse tree
	 */
	void enterLambdaParameter(KotlinParser.LambdaParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#lambdaParameter}.
	 * @param ctx the parse tree
	 */
	void exitLambdaParameter(KotlinParser.LambdaParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#objectLiteral}.
	 * @param ctx the parse tree
	 */
	void enterObjectLiteral(KotlinParser.ObjectLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#objectLiteral}.
	 * @param ctx the parse tree
	 */
	void exitObjectLiteral(KotlinParser.ObjectLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#collectionLiteral}.
	 * @param ctx the parse tree
	 */
	void enterCollectionLiteral(KotlinParser.CollectionLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#collectionLiteral}.
	 * @param ctx the parse tree
	 */
	void exitCollectionLiteral(KotlinParser.CollectionLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#thisExpression}.
	 * @param ctx the parse tree
	 */
	void enterThisExpression(KotlinParser.ThisExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#thisExpression}.
	 * @param ctx the parse tree
	 */
	void exitThisExpression(KotlinParser.ThisExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#superExpression}.
	 * @param ctx the parse tree
	 */
	void enterSuperExpression(KotlinParser.SuperExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#superExpression}.
	 * @param ctx the parse tree
	 */
	void exitSuperExpression(KotlinParser.SuperExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression(KotlinParser.ConditionalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression(KotlinParser.ConditionalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#ifExpression}.
	 * @param ctx the parse tree
	 */
	void enterIfExpression(KotlinParser.IfExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#ifExpression}.
	 * @param ctx the parse tree
	 */
	void exitIfExpression(KotlinParser.IfExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#controlStructureBody}.
	 * @param ctx the parse tree
	 */
	void enterControlStructureBody(KotlinParser.ControlStructureBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#controlStructureBody}.
	 * @param ctx the parse tree
	 */
	void exitControlStructureBody(KotlinParser.ControlStructureBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#whenExpression}.
	 * @param ctx the parse tree
	 */
	void enterWhenExpression(KotlinParser.WhenExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#whenExpression}.
	 * @param ctx the parse tree
	 */
	void exitWhenExpression(KotlinParser.WhenExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#whenEntry}.
	 * @param ctx the parse tree
	 */
	void enterWhenEntry(KotlinParser.WhenEntryContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#whenEntry}.
	 * @param ctx the parse tree
	 */
	void exitWhenEntry(KotlinParser.WhenEntryContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#whenCondition}.
	 * @param ctx the parse tree
	 */
	void enterWhenCondition(KotlinParser.WhenConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#whenCondition}.
	 * @param ctx the parse tree
	 */
	void exitWhenCondition(KotlinParser.WhenConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#rangeTest}.
	 * @param ctx the parse tree
	 */
	void enterRangeTest(KotlinParser.RangeTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#rangeTest}.
	 * @param ctx the parse tree
	 */
	void exitRangeTest(KotlinParser.RangeTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#typeTest}.
	 * @param ctx the parse tree
	 */
	void enterTypeTest(KotlinParser.TypeTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#typeTest}.
	 * @param ctx the parse tree
	 */
	void exitTypeTest(KotlinParser.TypeTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#tryExpression}.
	 * @param ctx the parse tree
	 */
	void enterTryExpression(KotlinParser.TryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#tryExpression}.
	 * @param ctx the parse tree
	 */
	void exitTryExpression(KotlinParser.TryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#catchBlock}.
	 * @param ctx the parse tree
	 */
	void enterCatchBlock(KotlinParser.CatchBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#catchBlock}.
	 * @param ctx the parse tree
	 */
	void exitCatchBlock(KotlinParser.CatchBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#finallyBlock}.
	 * @param ctx the parse tree
	 */
	void enterFinallyBlock(KotlinParser.FinallyBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#finallyBlock}.
	 * @param ctx the parse tree
	 */
	void exitFinallyBlock(KotlinParser.FinallyBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#loopExpression}.
	 * @param ctx the parse tree
	 */
	void enterLoopExpression(KotlinParser.LoopExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#loopExpression}.
	 * @param ctx the parse tree
	 */
	void exitLoopExpression(KotlinParser.LoopExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#forExpression}.
	 * @param ctx the parse tree
	 */
	void enterForExpression(KotlinParser.ForExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#forExpression}.
	 * @param ctx the parse tree
	 */
	void exitForExpression(KotlinParser.ForExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#whileExpression}.
	 * @param ctx the parse tree
	 */
	void enterWhileExpression(KotlinParser.WhileExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#whileExpression}.
	 * @param ctx the parse tree
	 */
	void exitWhileExpression(KotlinParser.WhileExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#doWhileExpression}.
	 * @param ctx the parse tree
	 */
	void enterDoWhileExpression(KotlinParser.DoWhileExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#doWhileExpression}.
	 * @param ctx the parse tree
	 */
	void exitDoWhileExpression(KotlinParser.DoWhileExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#jumpExpression}.
	 * @param ctx the parse tree
	 */
	void enterJumpExpression(KotlinParser.JumpExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#jumpExpression}.
	 * @param ctx the parse tree
	 */
	void exitJumpExpression(KotlinParser.JumpExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#callableReference}.
	 * @param ctx the parse tree
	 */
	void enterCallableReference(KotlinParser.CallableReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#callableReference}.
	 * @param ctx the parse tree
	 */
	void exitCallableReference(KotlinParser.CallableReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator(KotlinParser.AssignmentOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator(KotlinParser.AssignmentOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#equalityOperation}.
	 * @param ctx the parse tree
	 */
	void enterEqualityOperation(KotlinParser.EqualityOperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#equalityOperation}.
	 * @param ctx the parse tree
	 */
	void exitEqualityOperation(KotlinParser.EqualityOperationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void enterComparisonOperator(KotlinParser.ComparisonOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void exitComparisonOperator(KotlinParser.ComparisonOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#inOperator}.
	 * @param ctx the parse tree
	 */
	void enterInOperator(KotlinParser.InOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#inOperator}.
	 * @param ctx the parse tree
	 */
	void exitInOperator(KotlinParser.InOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#isOperator}.
	 * @param ctx the parse tree
	 */
	void enterIsOperator(KotlinParser.IsOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#isOperator}.
	 * @param ctx the parse tree
	 */
	void exitIsOperator(KotlinParser.IsOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#additiveOperator}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveOperator(KotlinParser.AdditiveOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#additiveOperator}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveOperator(KotlinParser.AdditiveOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#multiplicativeOperation}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeOperation(KotlinParser.MultiplicativeOperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#multiplicativeOperation}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeOperation(KotlinParser.MultiplicativeOperationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#typeOperation}.
	 * @param ctx the parse tree
	 */
	void enterTypeOperation(KotlinParser.TypeOperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#typeOperation}.
	 * @param ctx the parse tree
	 */
	void exitTypeOperation(KotlinParser.TypeOperationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#prefixUnaryOperation}.
	 * @param ctx the parse tree
	 */
	void enterPrefixUnaryOperation(KotlinParser.PrefixUnaryOperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#prefixUnaryOperation}.
	 * @param ctx the parse tree
	 */
	void exitPrefixUnaryOperation(KotlinParser.PrefixUnaryOperationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#postfixUnaryOperation}.
	 * @param ctx the parse tree
	 */
	void enterPostfixUnaryOperation(KotlinParser.PostfixUnaryOperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#postfixUnaryOperation}.
	 * @param ctx the parse tree
	 */
	void exitPostfixUnaryOperation(KotlinParser.PostfixUnaryOperationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#memberAccessOperator}.
	 * @param ctx the parse tree
	 */
	void enterMemberAccessOperator(KotlinParser.MemberAccessOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#memberAccessOperator}.
	 * @param ctx the parse tree
	 */
	void exitMemberAccessOperator(KotlinParser.MemberAccessOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#modifierList}.
	 * @param ctx the parse tree
	 */
	void enterModifierList(KotlinParser.ModifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#modifierList}.
	 * @param ctx the parse tree
	 */
	void exitModifierList(KotlinParser.ModifierListContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifier(KotlinParser.ModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifier(KotlinParser.ModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#classModifier}.
	 * @param ctx the parse tree
	 */
	void enterClassModifier(KotlinParser.ClassModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#classModifier}.
	 * @param ctx the parse tree
	 */
	void exitClassModifier(KotlinParser.ClassModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#memberModifier}.
	 * @param ctx the parse tree
	 */
	void enterMemberModifier(KotlinParser.MemberModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#memberModifier}.
	 * @param ctx the parse tree
	 */
	void exitMemberModifier(KotlinParser.MemberModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#visibilityModifier}.
	 * @param ctx the parse tree
	 */
	void enterVisibilityModifier(KotlinParser.VisibilityModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#visibilityModifier}.
	 * @param ctx the parse tree
	 */
	void exitVisibilityModifier(KotlinParser.VisibilityModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#varianceAnnotation}.
	 * @param ctx the parse tree
	 */
	void enterVarianceAnnotation(KotlinParser.VarianceAnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#varianceAnnotation}.
	 * @param ctx the parse tree
	 */
	void exitVarianceAnnotation(KotlinParser.VarianceAnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#functionModifier}.
	 * @param ctx the parse tree
	 */
	void enterFunctionModifier(KotlinParser.FunctionModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#functionModifier}.
	 * @param ctx the parse tree
	 */
	void exitFunctionModifier(KotlinParser.FunctionModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#propertyModifier}.
	 * @param ctx the parse tree
	 */
	void enterPropertyModifier(KotlinParser.PropertyModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#propertyModifier}.
	 * @param ctx the parse tree
	 */
	void exitPropertyModifier(KotlinParser.PropertyModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#inheritanceModifier}.
	 * @param ctx the parse tree
	 */
	void enterInheritanceModifier(KotlinParser.InheritanceModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#inheritanceModifier}.
	 * @param ctx the parse tree
	 */
	void exitInheritanceModifier(KotlinParser.InheritanceModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#parameterModifier}.
	 * @param ctx the parse tree
	 */
	void enterParameterModifier(KotlinParser.ParameterModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#parameterModifier}.
	 * @param ctx the parse tree
	 */
	void exitParameterModifier(KotlinParser.ParameterModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#typeParameterModifier}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameterModifier(KotlinParser.TypeParameterModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#typeParameterModifier}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameterModifier(KotlinParser.TypeParameterModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#labelDefinition}.
	 * @param ctx the parse tree
	 */
	void enterLabelDefinition(KotlinParser.LabelDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#labelDefinition}.
	 * @param ctx the parse tree
	 */
	void exitLabelDefinition(KotlinParser.LabelDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#annotations}.
	 * @param ctx the parse tree
	 */
	void enterAnnotations(KotlinParser.AnnotationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#annotations}.
	 * @param ctx the parse tree
	 */
	void exitAnnotations(KotlinParser.AnnotationsContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#annotation}.
	 * @param ctx the parse tree
	 */
	void enterAnnotation(KotlinParser.AnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#annotation}.
	 * @param ctx the parse tree
	 */
	void exitAnnotation(KotlinParser.AnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#annotationList}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationList(KotlinParser.AnnotationListContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#annotationList}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationList(KotlinParser.AnnotationListContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#annotationUseSiteTarget}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationUseSiteTarget(KotlinParser.AnnotationUseSiteTargetContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#annotationUseSiteTarget}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationUseSiteTarget(KotlinParser.AnnotationUseSiteTargetContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#unescapedAnnotation}.
	 * @param ctx the parse tree
	 */
	void enterUnescapedAnnotation(KotlinParser.UnescapedAnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#unescapedAnnotation}.
	 * @param ctx the parse tree
	 */
	void exitUnescapedAnnotation(KotlinParser.UnescapedAnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(KotlinParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(KotlinParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#simpleIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterSimpleIdentifier(KotlinParser.SimpleIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#simpleIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitSimpleIdentifier(KotlinParser.SimpleIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#semi}.
	 * @param ctx the parse tree
	 */
	void enterSemi(KotlinParser.SemiContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#semi}.
	 * @param ctx the parse tree
	 */
	void exitSemi(KotlinParser.SemiContext ctx);
	/**
	 * Enter a parse tree produced by {@link KotlinParser#anysemi}.
	 * @param ctx the parse tree
	 */
	void enterAnysemi(KotlinParser.AnysemiContext ctx);
	/**
	 * Exit a parse tree produced by {@link KotlinParser#anysemi}.
	 * @param ctx the parse tree
	 */
	void exitAnysemi(KotlinParser.AnysemiContext ctx);
}