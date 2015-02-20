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

import com.liferay.ide.project.ui.ProjectUI;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;


public class ComponentPropertyCompletionProposal implements ICompletionProposal
{

    private final String displayString;
    private final int replacementOffset;
    private final String replacementString;
    private final int replacementLength;
    private final int cursorPosition;

    public ComponentPropertyCompletionProposal(
        String displayString, int replacementOffset, String replacementString, int replacementLength,
        int cursorPosition )
    {
        this.displayString = displayString;
        this.replacementOffset = replacementOffset;
        this.replacementString = replacementString;
        this.replacementLength = replacementLength;
        this.cursorPosition = cursorPosition;
    }

    @Override
    public void apply( IDocument document )
    {
        try {
            document.get(replacementOffset - 1, 1);
        } catch (BadLocationException _ex) {
        }
        CompletionProposal proposal = new CompletionProposal(
                replacementString, replacementOffset,
                replacementLength, cursorPosition, getImage(),
                getDisplayString(), getContextInformation(),
                getAdditionalProposalInfo());
        proposal.apply(document);
    }



    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComponentPropertyCompletionProposal) {
            ComponentPropertyCompletionProposal proposal = (ComponentPropertyCompletionProposal) obj;
            return proposal.replacementOffset == this.replacementOffset &&
                    proposal.replacementString.equals( this.replacementString );
        }
        return false;
    }

    public Point getSelection( IDocument document )
    {
        CompletionProposal proposal =
            new CompletionProposal(
                replacementString, replacementOffset, replacementLength, cursorPosition, getImage(),
                getDisplayString(), getContextInformation(), null );
        return proposal.getSelection( document );
    }

    @Override
    public String getAdditionalProposalInfo()
    {
        return this.displayString + " documentation.";
    }

    @Override
    public String getDisplayString()
    {
        return this.displayString;
    }

    @Override
    public Image getImage()
    {
        return ProjectUI.getDefault().getImageRegistry().get( ProjectUI.IMAGE_PROPERTIES );
    }

    @Override
    public IContextInformation getContextInformation()
    {
        return null;
    }

}
