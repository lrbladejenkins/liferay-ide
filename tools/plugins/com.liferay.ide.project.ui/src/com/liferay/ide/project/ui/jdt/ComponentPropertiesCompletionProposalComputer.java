/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/
package com.liferay.ide.project.ui.jdt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.CompletionContext;
import org.eclipse.jdt.core.ICodeAssist;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnAnnotationOfType;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnStringLiteral;
import org.eclipse.jdt.internal.codeassist.complete.CompletionParser;
import org.eclipse.jdt.internal.codeassist.impl.AssistCompilationUnit;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class ComponentPropertiesCompletionProposalComputer implements IJavaCompletionProposalComputer
{

    private static class Tuple
    {
        final IType type;
        final int offset;

        Tuple( IType type, int offset )
        {
            this.type = type;
            this.offset = offset;
        }
    }

    @Override
    public void sessionStarted()
    {
    }

    private final List<String> validKeys = Arrays.asList(
        new String[] {
            "com.liferay.portlet.action-timeout", "com.liferay.portlet.active",
            "com.liferay.portlet.add-default-resource",
            "com.liferay.portlet.ajaxable",
            "com.liferay.portlet.autopropagated-parameters",
            "com.liferay.portlet.autopropagated-parameters",
            "com.liferay.portlet.configuration-path",
            "com.liferay.portlet.control-panel-entry-category",
            "com.liferay.portlet.control-panel-entry-weight",
            "com.liferay.portlet.css-class-wrapper",
            "com.liferay.portlet.display-category",
            "com.liferay.portlet.facebook-integration",
            "com.liferay.portlet.footer-portal-css",
            "com.liferay.portlet.footer-portal-javascript",
            "com.liferay.portlet.footer-portlet-css",
            "com.liferay.portlet.footer-portlet-javascript",
            "com.liferay.portlet.friendly-url-mapping",
            "com.liferay.portlet.friendly-url-routes",
            "com.liferay.portlet.header-portal-css",
            "com.liferay.portlet.header-portal-javascript",
            "com.liferay.portlet.header-portlet-css",
            "com.liferay.portlet.header-portlet-javascript",
            "com.liferay.portlet.icon", "com.liferay.portlet.instanceable",
            "com.liferay.portlet.layout-cacheable",
            "com.liferay.portlet.maximize-edit",
            "com.liferay.portlet.maximize-help",
            "com.liferay.portlet.parent-struts-path",
            "com.liferay.portlet.pop-up-print",
            "com.liferay.portlet.preferences-company-wide",
            "com.liferay.portlet.preferences-owned-by-group",
            "com.liferay.portlet.preferences-unique-per-layout",
            "com.liferay.portlet.private-request-attributes",
            "com.liferay.portlet.private-session-attributes",
            "com.liferay.portlet.remoteable",
            "com.liferay.portlet.render-timeout",
            "com.liferay.portlet.render-weight",
            "com.liferay.portlet.requires-namespaced-parameters",
            "com.liferay.portlet.restore-current-view",
            "com.liferay.portlet.scopeable",
            "com.liferay.portlet.show-portlet-access-denied",
            "com.liferay.portlet.show-portlet-inactive",
            "com.liferay.portlet.show-portlet-inactive",
            "com.liferay.portlet.single-page-application",
            "com.liferay.portlet.struts-path", "com.liferay.portlet.system",
            "com.liferay.portlet.use-default-template",
            "com.liferay.portlet.user-principal-strategy",
            "com.liferay.portlet.virtual-path", "javax.portlet.description",
            "javax.portlet.display-name", "javax.portlet.expiration-cache",
            "javax.portlet.info.keywords", "javax.portlet.info.short-title",
            "javax.portlet.info.title", "javax.portlet.portlet-mode",
            "javax.portlet.portlet-name", "javax.portlet.preferences",
            "javax.portlet.resource-bundle", "javax.portlet.security-role-ref",
            "javax.portlet.supported-processing-event",
            "javax.portlet.supported-public-render-parameter",
            "javax.portlet.supported-publishing-event",
            "javax.portlet.window-state"
        });

    @Override
    public List<ICompletionProposal> computeCompletionProposals(
        ContentAssistInvocationContext context, IProgressMonitor monitor )
    {
        List<ICompletionProposal> retval = new ArrayList<ICompletionProposal>();

        try
        {
            if( isComponentPropertyCompletion( context ) )
            {
                CompletionContext core = ( (JavaContentAssistInvocationContext) context ).getCoreContext();
                String token = new String( core.getToken() );
                String[] parts = token.split( "=" );
                String key = parts[0];

                for( String validKey : validKeys )
                {
                    if( key.length() == 0 || validKey.startsWith( key ) )
                    {
                        String replacementString = validKey;

                        if( key.length() > 0 )
                        {
                            replacementString = validKey.substring( key.length() );
                        }

                        retval.add( new ComponentPropertyCompletionProposal(
                            validKey, context.getInvocationOffset(), replacementString, 0,
                            replacementString.length() ) );
                    }
                }
            }
        }
        catch( JavaModelException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return retval;
    }

    private boolean isComponentPropertyCompletion( ContentAssistInvocationContext context ) throws JavaModelException
    {
        if( context instanceof JavaContentAssistInvocationContext )
        {
            final JavaContentAssistInvocationContext javaContext = (JavaContentAssistInvocationContext) context;
            final int startingOffset = javaContext.getInvocationOffset();
            final IJavaElement element = javaContext.getCoreContext().getEnclosingElement();
            final CompilerOptions options = new CompilerOptions( JavaCore.getOptions() );
            final ProblemReporter reporter =
                new ProblemReporter(
                    DefaultErrorHandlingPolicies.proceedWithAllProblems(), options,
                    new DefaultProblemFactory() );
            final CompletionParser parser = new CompletionParser( reporter, false );

            if( element instanceof AssistCompilationUnit )
            {
                AssistCompilationUnit assist = (AssistCompilationUnit) element;

                final Tuple typeAndOffset = rewindToFindFirstType( assist, startingOffset );

                if( typeAndOffset.type.getElementName().equals( "Component" ) )
                {
                    final CompilationResult result =
                        new CompilationResult( assist, 0, 0, options.maxProblemsPerUnit );

                    final CompletionOnAnnotationOfType annotType =
                        rewindToFindFirstAnnotationType( parser, assist, result, startingOffset, typeAndOffset.offset );

                    if( annotType != null )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private CompletionOnAnnotationOfType rewindToFindFirstAnnotationType(
        CompletionParser parser, ICompilationUnit cu, CompilationResult result, int startingOffset, int endingOffset )
    {
        for( int offset = startingOffset; offset >= endingOffset; offset--)
        {
            final CompilationUnitDeclaration parsed = parser.dietParse( cu, result, offset );

            if( parsed.types.length > 0 )
            {
                if( parsed.types[0] instanceof CompletionOnAnnotationOfType )
                {
                    CompletionOnAnnotationOfType annotType = (CompletionOnAnnotationOfType) parsed.types[0];

                    if( annotType != null && annotType.annotations.length > 0 &&
                        annotType.annotations[0] instanceof NormalAnnotation )
                    {
                        NormalAnnotation annot =
                            (NormalAnnotation) annotType.annotations[0];

                        for( MemberValuePair pair : annot.memberValuePairs)
                        {
                            final String name = new String( pair.name );

                            if( ( "property".equals( name ) || "properties".equals( name ) )  && pair.value instanceof CompletionOnStringLiteral )
                            {
                                return annotType;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private Tuple rewindToFindFirstType( ICodeAssist assist , int startingOffset ) throws JavaModelException
    {
        for( int offset = startingOffset; offset > -1; offset--)
        {
            IJavaElement[] code = assist.codeSelect( offset, 0 );

            if( code != null && code.length > 0 )
            {
                if( code[0] instanceof IType )
                {
                    return new Tuple( (IType) code[0], offset );
                }
            }
        }

        return null;
    }

    @Override
    public List<IContextInformation> computeContextInformation(
        ContentAssistInvocationContext context, IProgressMonitor monitor )
    {
        try
        {
            System.out.println(context.getDocument().getContentType( context.getInvocationOffset() ));
        }
        catch( BadLocationException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public String getErrorMessage()
    {
        return null;
    }

    @Override
    public void sessionEnded()
    {
    }

}
