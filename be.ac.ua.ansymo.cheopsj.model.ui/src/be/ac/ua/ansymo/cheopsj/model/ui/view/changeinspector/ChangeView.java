/*******************************************************************************
 * Copyright (c) 2011 Quinten David Soetens
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Quinten David Soetens - initial API and implementation
 ******************************************************************************/
package be.ac.ua.ansymo.cheopsj.model.ui.view.changeinspector;

import java.util.Comparator;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;


public class ChangeView extends ViewPart {

	public static final String ID = "be.ac.ua.ansymo.cheopsj.model.ui.view.changeinspector.ChangeView";

	private TableViewer viewer;
	private TableColumn changeTypeCol;
	private TableColumn famixTypeCol;
	private TableColumn nameCol;
	private TableColumn timestampCol;
	private TableColumn userCol;
	private TableColumn intentCol;
	private ChangeSorter sorter;
	
	private ChangeViewContentProvider contentProvider;
	private ChangeViewLabelProvider labelProvider;

	public ChangeView() {
		contentProvider = new ChangeViewContentProvider(this);
		labelProvider = new ChangeViewLabelProvider();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {		
		createTableViewer(parent);
		createTableSorter();
		createToolbarButtons();
		
		updateSummaryBreakdown();
		
	}

	public void updateSummaryBreakdown() {
		String breakdown = contentProvider.getSummary();
		setContentDescription(breakdown);
	}

	private void createTableViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		final Table table = viewer.getTable();

		TableColumnLayout layout = new TableColumnLayout();
		parent.setLayout(layout);

		changeTypeCol = new TableColumn(table, SWT.LEFT);
		changeTypeCol.setText("ChangeType");
		layout.setColumnData(changeTypeCol, new ColumnWeightData(6));

		famixTypeCol = new TableColumn(table, SWT.LEFT);
		famixTypeCol.setText("FamixType");
		layout.setColumnData(famixTypeCol, new ColumnWeightData(6));

		nameCol = new TableColumn(table, SWT.LEFT);
		nameCol.setText("Name");
		layout.setColumnData(nameCol, new ColumnWeightData(6));

		timestampCol = new TableColumn(table, SWT.LEFT);
		timestampCol.setText("Time");
		layout.setColumnData(timestampCol, new ColumnWeightData(6));
		
		userCol = new TableColumn(table, SWT.LEFT);
		userCol.setText("User");
		layout.setColumnData(userCol, new ColumnWeightData(6));
		
		intentCol = new TableColumn(table, SWT.LEFT);
		intentCol.setText("Intent");
		layout.setColumnData(intentCol, new ColumnWeightData(6));

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(labelProvider);
		viewer.setInput(ModelManager.getInstance());

		getSite().setSelectionProvider(viewer);
	}

	@SuppressWarnings("unchecked")
	private void createTableSorter() {
		Comparator<IChange> changeTypeComparator = new Comparator<IChange>() {
			public int compare(IChange i1, IChange i2) {
				return i1.getChangeType().compareTo(i2.getChangeType());
			}
		};
		Comparator<IChange> famixTypeComparator = new Comparator<IChange>() {
			public int compare(IChange i1, IChange i2) {
				return i1.getFamixType().compareTo(i2.getFamixType());
			}
		};
		Comparator<IChange> nameComparator = new Comparator<IChange>() {
			public int compare(IChange i1, IChange i2) {
				return i1.getName().compareTo(i2.getName());
			}
		};
		Comparator<IChange> timeComparator = new Comparator<IChange>() {
			public int compare(IChange i1, IChange i2) {
				return i1.getTimeStamp().compareTo(i2.getTimeStamp());
			}
		};
		Comparator<IChange> userComparator = new Comparator<IChange>() {
			public int compare(IChange i1, IChange i2) {
					return i1.getUser().compareTo(i2.getUser());
			}
		};
		Comparator<IChange> intentComparator = new Comparator<IChange>() {
			public int compare(IChange i1, IChange i2) {
				return i1.getIntent().compareTo(i2.getIntent());
			}
		};
		sorter = new ChangeSorter(viewer, new TableColumn[] {changeTypeCol, famixTypeCol, nameCol, timestampCol, userCol, intentCol}, new Comparator[] {
				changeTypeComparator, famixTypeComparator, nameComparator, timeComparator, userComparator, intentComparator });
		viewer.setSorter(sorter);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * For testing purposes only.
	 * 
	 * @return the table viewer in the Change view
	 */
	public TableViewer getChangeViewer() {
		return viewer;
	}

	private void createToolbarButtons() {
		IToolBarManager toolBarMgr = getViewSite().getActionBars().getToolBarManager();
		toolBarMgr.add(new GroupMarker("persistency"));
	}

	public IStructuredSelection getSelection() {
		return (IStructuredSelection) viewer.getSelection();
	}
}