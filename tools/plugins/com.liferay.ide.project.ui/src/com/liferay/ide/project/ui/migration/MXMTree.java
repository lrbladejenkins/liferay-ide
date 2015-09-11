
package com.liferay.ide.project.ui.migration;

/**
 * @author Gregory Amerson
 */
public class MXMTree
{
    MXMNode root;
    MXMNode commonRoot;

    public MXMTree( MXMNode root )
    {
        this.root = root;
        commonRoot = null;
    }

    public void addElement( String elementValue )
    {
        String[] list = elementValue.split( "/" );

        // latest element of the list is the filename.extrension
        root.addElement( root.incrementalPath, list );
    }

    public MXMNode getCommonRoot()
    {
        if( commonRoot != null )
        {
            return commonRoot;
        }
        else
        {
            MXMNode current = root;

            while( current.leafs.size() <= 0 && current.childs.size() == 1 )
            {
                current = current.childs.get( 0 );
            }

            commonRoot = current;

            return commonRoot;
        }

    }

}
