package com.redpois0n.pathtree;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

@SuppressWarnings("serial")
public class PathJTree extends JTree implements TreeExpansionListener, MouseListener {
	
	private final List<NodeClickListener> leafListeners = new ArrayList<NodeClickListener>();
	private final List<NodeClickListener> folderListeners = new ArrayList<NodeClickListener>();

	private String delimiter;

	public PathJTree() {
		this(new PathTreeModel(new FolderTreeNode("root", FileIconUtils.getFolderIcon())), "/");
	}
	
	public PathJTree(TreeModel model, String delimiter) {
		super(model);
		super.setShowsRootHandles(true);
		super.setCellRenderer(new PathTreeRenderer());
		super.addTreeExpansionListener(this);
		super.addMouseListener(this);
		this.delimiter = delimiter;
	}
	
	public PathTreeModel getPathModel() {
		return (PathTreeModel) super.getModel();
	}
	
	public PathTreeNode getSelectedNode() {
		return getSelectionPath().getLastPathComponent() == null ? null : (PathTreeNode) getSelectionPath().getLastPathComponent();
	}
	
	public void addRoot(DefaultMutableTreeNode root) {
		getPathModel().addRoot(root);
	}
	
	public void addFileClickListener(NodeClickListener l) {
		leafListeners.add(l);
	}
	
	public void remoteFileClickListener(NodeClickListener l) {
		leafListeners.remove(l);
	}
	
	public void addFolderClickListener(NodeClickListener l) {
		folderListeners.add(l);
	}
	
	public void removeFolderClickListener(NodeClickListener l) {
		folderListeners.remove(l);
	}
	
	public String getDelimiter() {
		return this.delimiter;
	}
	
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	public void expandAll() {
		for (int i = 0; i < getRowCount(); i++) {
			expandRow(i);
		}
	}
	
	public String makePath(TreePath p) {
		String path = "";
		
		for (Object obj : p.getPath()) {
			if (obj instanceof PathTreeNode && obj != getPathModel().getRootNode()) {
				PathTreeNode node = (PathTreeNode) obj;
				path += node.toString() + delimiter;
			}
		}
		
		if (path.length() > 0) {
			path = path.substring(0, path.length() - 1);
		}
		
		return path;
	}
	
	public PathTreeNode getNodeFromPath(String path) {		
		for (int i = 0; i < getRowCount(); i++) {
			TreePath treePath = getPathForRow(i);
			String mpath = makePath(treePath);

			if (mpath.equalsIgnoreCase(path)) {
				return (PathTreeNode) treePath.getLastPathComponent();
			}
		}
		
		return null;
	}

	public void insertFakeNode(PathTreeNode insertedNode) {
		getPathModel().insertNodeInto(new PlaceHolderTreeNode(), insertedNode, 0);		
	}
	
	@SuppressWarnings("unchecked")
	public boolean exists(String s) {
	    Enumeration<DefaultMutableTreeNode> e = ((DefaultMutableTreeNode) getPathModel().getRootNode()).depthFirstEnumeration();
	    while (e.hasMoreElements()) {
	        DefaultMutableTreeNode node = e.nextElement();
	        String path = makePath(new TreePath(node.getPath()));
	        if (s.equalsIgnoreCase(path)) {
	            return true;
	        }
	    }
	    return false;
	}

	@Override
	public void treeExpanded(TreeExpansionEvent event) {
		TreePath tp = event.getPath();

		if (tp != null && tp.getLastPathComponent() != null) {
	    	DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
		    
		    for (int i = 0; i < node.getChildCount(); i++) {
		    	DefaultMutableTreeNode n = (DefaultMutableTreeNode) node.getChildAt(i);
		    	if (n instanceof PlaceHolderTreeNode) {
		    		getPathModel().removeNodeFromParent(n);
		    	}
		    }
	    }

	    if (tp != null) {
	    	String path = PathJTree.this.makePath(tp);
	    	
	    	for (NodeClickListener l : folderListeners) {
	    		l.itemSelected((PathTreeNode) tp.getLastPathComponent(), path);
	    	}
	    }
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent event) {
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		TreePath tp = PathJTree.this.getPathForLocation(e.getX(), e.getY());

        if (tp != null && tp.getLastPathComponent() != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
            
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) node.getChildAt(i);
                if (n instanceof PlaceHolderTreeNode) {
                    getPathModel().removeNodeFromParent(n);
                }
            }
        }

        if (tp != null && tp.getLastPathComponent() instanceof FileTreeNode && e.getClickCount() == 2) {
            String path = PathJTree.this.makePath(tp);
            
            for (NodeClickListener l : leafListeners) {
	    		l.itemSelected((PathTreeNode) tp.getLastPathComponent(), path);
        	}	
        } else if (tp != null && tp.getLastPathComponent() instanceof FolderTreeNode) {
            String path = PathJTree.this.makePath(tp);

        	for (NodeClickListener l : folderListeners) {
	    		l.itemSelected((PathTreeNode) tp.getLastPathComponent(), path);
	    	}
        }
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
}
