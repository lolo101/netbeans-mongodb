/*
 * The MIT License
 *
 * Copyright 2013 Yann.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.netbeans.modules.mongodb.ui.windows;

import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.AddDocumentAction;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.awt.CardLayout;
import org.netbeans.modules.mongodb.CollectionInfo;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.PlainDocument;
import javax.swing.tree.TreePath;
import lombok.Getter;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.netbeans.modules.mongodb.ConnectionInfo;
import org.netbeans.modules.mongodb.DbInfo;
import org.netbeans.modules.mongodb.resources.Images;
import org.netbeans.modules.mongodb.options.JsonCellRenderingOptions;
import org.netbeans.modules.mongodb.options.LabelCategory;
import org.netbeans.modules.mongodb.ui.components.QueryEditor;
import org.netbeans.modules.mongodb.ui.util.IntegerDocumentFilter;
import org.netbeans.modules.mongodb.ui.windows.collectionview.CollectionQueryResult;
import org.netbeans.modules.mongodb.ui.windows.collectionview.CollectionQueryResultView;
import org.netbeans.modules.mongodb.ui.windows.collectionview.CollectionQueryResultUpdateListener;
import org.netbeans.modules.mongodb.ui.windows.collectionview.flattable.JsonFlatTableCellRenderer;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.ChangeResultViewAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.ClearQueryAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.CollapseAllDocumentsAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.CopyDocumentToClipboardAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.CopyKeyToClipboardAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.CopyValueToClipboardAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.DeleteSelectedDocumentAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.EditQueryAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.EditSelectedDocumentAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.ExpandAllDocumentsAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.ExportQueryResultAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.NavFirstAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.NavLastAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.NavLeftAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.NavRightAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.actions.RefreshDocumentsAction;
import org.netbeans.modules.mongodb.ui.windows.collectionview.flattable.DocumentsFlatTableModel;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.CollectionViewTreeTableNode;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.DocumentNode;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.DocumentTreeTableHighlighter;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.JsonPropertyNode;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.JsonTreeTableCellRenderer;
import org.netbeans.modules.mongodb.ui.windows.collectionview.treetable.DocumentsTreeTableModel;
import org.netbeans.modules.mongodb.util.JsonProperty;
import org.netbeans.modules.mongodb.util.SystemCollectionPredicate;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@TopComponent.Description(
    preferredID = "CollectionView",
    persistenceType = TopComponent.PERSISTENCE_NEVER)
@Messages({
    "invalidJson=invalid json",
    "# {0} - total documents count",
    "totalDocuments=Total Documents: {0}      ",
    "# {0} - current page",
    "# {1} - total page count",
    "pageCountLabel=Page {0} of {1}",
    "# {0} - db name",
    "# {1} - collection name",
    "collectionViewTitle={0}.{1}",
    "# {0} - connection name",
    "# {1} - view title",
    "collectionViewTooltip={0}: {1}"
})
public final class CollectionView extends TopComponent {

    private static final ResultView DEFAULT_RESULT_VIEW = ResultView.TREE_TABLE;

    private final boolean isSystemCollection;

    @Getter
    private final QueryEditor queryEditor = new QueryEditor();

    @Getter
    private final CollectionQueryResult collectionQueryResult;

    private final Map<ResultView, JToggleButton> resultViewButtons;

    private ResultView resultView = DEFAULT_RESULT_VIEW;

    private final Map<ResultView, CollectionQueryResultUpdateListener> resultViews = new EnumMap<>(ResultView.class);

    private Lookup lookup;
    
    public CollectionView(CollectionInfo collectionInfo, Lookup lookup) {
        super(lookup);
        this.lookup = lookup;
        isSystemCollection = SystemCollectionPredicate.get().eval(collectionInfo.getName());
        initComponents();
        updateTitle();
        setIcon(isSystemCollection
            ? Images.SYSTEM_COLLECTION_ICON
            : Images.COLLECTION_ICON);

        resultViewButtons = new EnumMap<>(ResultView.class);
        resultViewButtons.put(ResultView.FLAT_TABLE, flatTableViewButton);
        resultViewButtons.put(ResultView.TREE_TABLE, treeTableViewButton);

        final DBCollection dbCollection = lookup.lookup(DBCollection.class);
        collectionQueryResult = new CollectionQueryResult(dbCollection);
        final DocumentsTreeTableModel treeTableModel = new DocumentsTreeTableModel(collectionQueryResult);
        final DocumentsFlatTableModel flatTableModel = new DocumentsFlatTableModel(collectionQueryResult);
        resultViews.put(ResultView.TREE_TABLE, treeTableModel);
        resultViews.put(ResultView.FLAT_TABLE, flatTableModel);

        final ListSelectionListener tableSelectionListener = new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    updateDocumentButtonsState();
                }
            }
        };

        resultFlatTable.setModel(flatTableModel);
        resultFlatTable.setDefaultRenderer(DBObject.class, new JsonFlatTableCellRenderer());
        resultFlatTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultFlatTable.getSelectionModel().addListSelectionListener(tableSelectionListener);
        resultFlatTable.getColumnModel().addColumnModelListener(new TableColumnModelListener() {

            @Override
            public void columnAdded(TableColumnModelEvent e) {
                final TableColumnModel model = (TableColumnModel) e.getSource();
                final TableColumn column = model.getColumn(e.getToIndex());
                if ("_id".equals(column.getHeaderValue())) {
                    final Font font = JsonCellRenderingOptions.INSTANCE
                        .getLabelFontConf(LabelCategory.DOCUMENT).getFont();
                    final int preferredWidth = getFontMetrics(font)
                        .stringWidth("000000000000000000000000");
                    column.setPreferredWidth(preferredWidth);
                }
            }

            @Override
            public void columnRemoved(TableColumnModelEvent e) {
            }

            @Override
            public void columnMoved(TableColumnModelEvent e) {
            }

            @Override
            public void columnMarginChanged(ChangeEvent e) {
            }

            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {
            }
        });

        resultTreeTable.setTreeTableModel(treeTableModel);
        resultTreeTable.setTreeCellRenderer(new JsonTreeTableCellRenderer());
        resultTreeTable.addHighlighter(new DocumentTreeTableHighlighter());
        resultTreeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultTreeTable.getSelectionModel().addListSelectionListener(tableSelectionListener);

        final PlainDocument document = (PlainDocument) pageSizeField.getDocument();
        document.setDocumentFilter(new IntegerDocumentFilter());

        resultTreeTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    final TreePath path = resultTreeTable.getPathForLocation(e.getX(), e.getY());
                    final TreeTableNode node = (TreeTableNode) path.getLastPathComponent();
                    if (node.isLeaf()) {
                        editSelectedDocumentAction.actionPerformed(null);
                    } else {
                        if (resultTreeTable.isCollapsed(path)) {
                            resultTreeTable.expandPath(path);
                        } else {
                            resultTreeTable.collapsePath(path);
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final TreePath path = resultTreeTable.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        final int row = resultTreeTable.getRowForPath(path);
                        resultTreeTable.setRowSelectionInterval(row, row);
                    }
                    final JPopupMenu menu = createTreeTableContextMenu(path);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

        });
        resultFlatTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    editSelectedDocumentAction.actionPerformed(null);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final int row = resultFlatTable.rowAtPoint(e.getPoint());
                    if (row > -1) {
                        final int column = resultFlatTable.columnAtPoint(e.getPoint());
                        resultFlatTable.setRowSelectionInterval(row, row);
                        final JPopupMenu menu = createFlatTableContextMenu(row, column);
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }

        });
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    public void setLookup(Lookup lookup) {
        this.lookup = lookup;
        final DBCollection dbCollection = lookup.lookup(DBCollection.class);
        collectionQueryResult.setDbCollection(dbCollection);
    }
    
    public void updateTitle() {
        final ConnectionInfo connectionInfo = lookup.lookup(ConnectionInfo.class);
        final DbInfo dbInfo = lookup.lookup(DbInfo.class);
        final CollectionInfo collectionInfo = lookup.lookup(CollectionInfo.class);
        final String title = Bundle.collectionViewTitle(dbInfo.getDbName(), collectionInfo.getName());
        setName(title);
        setToolTipText(
            Bundle.collectionViewTooltip(connectionInfo.getDisplayName(), title));
    }

    @Override
    protected void componentShowing() {
        loadPreferences();
    }

    @Override
    protected void componentClosed() {
        writePreferences();
    }

    private JTable getResultTable() {
        switch (resultView) {
            case FLAT_TABLE:
                return resultFlatTable;
            case TREE_TABLE:
                return resultTreeTable;
            default:
                throw new AssertionError();
        }
    }

    public DBObject getResultTableSelectedDocument() {
        final JTable table = getResultTable();
        int row = table.getSelectedRow();
        if (row == -1) {
            return null;
        }
        switch (resultView) {
            case FLAT_TABLE:
                return ((DocumentsFlatTableModel) resultFlatTable.getModel()).getRowValue(row);
            case TREE_TABLE:
                final TreePath selectionPath = resultTreeTable.getPathForRow(row);
                final DocumentNode documentNode = (DocumentNode) selectionPath.getPathComponent(1);
                return (DBObject) resultTreeTable.getTreeTableModel().getValueAt(documentNode, 0);
            default:
                throw new AssertionError();
        }
    }

    public void changeResultView(ResultView resultView) {
        this.resultView = resultView;
        collectionQueryResult.setView(resultViews.get(resultView));
        collectionQueryResult.refreshViewIfNecessary();
        updateResultPanel();
    }

    private void updateResultPanel() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                final CardLayout layout = (CardLayout) resultPanel.getLayout();
                layout.show(resultPanel, resultView.name());
                final boolean treeView = resultView == ResultView.TREE_TABLE;
                collapseTreeAction.setEnabled(treeView);
                expandTreeAction.setEnabled(treeView);
            }
        });
    }

    public void refreshResults() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                collectionQueryResult.setPage(1);
                collectionQueryResult.update();
                updatePagination();
                updateDocumentButtonsState();
            }
        }).start();
    }

    public void updatePagination() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                totalDocumentsLabel.setText(
                    Bundle.totalDocuments(collectionQueryResult.getTotalDocumentsCount()));
                int page = collectionQueryResult.getPage();
                int pageCount = collectionQueryResult.getPageCount();
                pageCountLabel.setText(Bundle.pageCountLabel(page, pageCount));

                boolean leftNavEnabled = page > 1;
                navFirstAction.setEnabled(leftNavEnabled);
                navLeftAction.setEnabled(leftNavEnabled);
                boolean rightNavEnabled = page < pageCount;
                navRightAction.setEnabled(rightNavEnabled);
                navLastAction.setEnabled(rightNavEnabled);
            }
        });
    }

    private void updateDocumentButtonsState() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                boolean itemSelected = getResultTable().getSelectedRow() > -1;
                addButton.setEnabled(isSystemCollection == false);
                deleteButton.setEnabled(itemSelected && isSystemCollection == false);
                editButton.setEnabled(itemSelected && isSystemCollection == false);
            }
        });
    }

    public void updateQueryFieldsFromEditor() {
        final DBObject criteria = queryEditor.getCriteria();
        final DBObject projection = queryEditor.getProjection();
        final DBObject sort = queryEditor.getSort();
        criteriaField.setText(criteria != null ? JSON.serialize(criteria) : "");
        projectionField.setText(projection != null ? JSON.serialize(projection) : "");
        sortField.setText(sort != null ? JSON.serialize(sort) : "");
        collectionQueryResult.setCriteria(criteria);
        collectionQueryResult.setProjection(projection);
        collectionQueryResult.setSort(sort);
        refreshResults();
    }

    private void changePageSize(int pageSize) {
        ((CollectionQueryResultView) resultFlatTable.getModel())
            .getCollectionQueryResult().setPageSize(pageSize);
        ((CollectionQueryResultView) resultTreeTable.getTreeTableModel())
            .getCollectionQueryResult().setPageSize(pageSize);
        refreshResults();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        resultsViewButtonGroup = new javax.swing.ButtonGroup();
        queryPanel = new javax.swing.JPanel();
        criteriaLabel = new javax.swing.JLabel();
        criteriaField = new javax.swing.JTextField();
        projectionLabel = new javax.swing.JLabel();
        projectionField = new javax.swing.JTextField();
        sortLabel = new javax.swing.JLabel();
        sortField = new javax.swing.JTextField();
        editQueryButton = new javax.swing.JButton();
        clearQueryButton = new javax.swing.JButton();
        documentsToolBar = new javax.swing.JToolBar();
        treeTableViewButton = new javax.swing.JToggleButton();
        flatTableViewButton = new javax.swing.JToggleButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        expandTreeButton = new javax.swing.JButton();
        collapseTreeButton = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        refreshDocumentsButton = new javax.swing.JButton();
        navFirstButton = new javax.swing.JButton();
        navLeftButton = new javax.swing.JButton();
        navRightButton = new javax.swing.JButton();
        navLastButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        pageSizeLabel = new javax.swing.JLabel();
        pageSizeField = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        totalDocumentsLabel = new javax.swing.JLabel();
        pageCountLabel = new javax.swing.JLabel();
        resultPanel = new javax.swing.JPanel();
        treeTableScrollPane = new javax.swing.JScrollPane();
        resultTreeTable = new org.jdesktop.swingx.JXTreeTable();
        flatTableScrollPane = new javax.swing.JScrollPane();
        resultFlatTable = new javax.swing.JTable();

        queryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CollectionView.class, "CollectionView.queryPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(criteriaLabel, org.openide.util.NbBundle.getMessage(CollectionView.class, "CollectionView.criteriaLabel.text")); // NOI18N

        criteriaField.setEditable(false);
        criteriaField.setText(org.openide.util.NbBundle.getMessage(CollectionView.class, "CollectionView.criteriaField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(projectionLabel, org.openide.util.NbBundle.getMessage(CollectionView.class, "CollectionView.projectionLabel.text")); // NOI18N

        projectionField.setEditable(false);
        projectionField.setText(org.openide.util.NbBundle.getMessage(CollectionView.class, "CollectionView.projectionField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(sortLabel, org.openide.util.NbBundle.getMessage(CollectionView.class, "CollectionView.sortLabel.text")); // NOI18N

        sortField.setEditable(false);
        sortField.setText(org.openide.util.NbBundle.getMessage(CollectionView.class, "CollectionView.sortField.text")); // NOI18N

        editQueryButton.setAction(getEditQueryAction());

        clearQueryButton.setAction(getClearQueryAction());

        javax.swing.GroupLayout queryPanelLayout = new javax.swing.GroupLayout(queryPanel);
        queryPanel.setLayout(queryPanelLayout);
        queryPanelLayout.setHorizontalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(queryPanelLayout.createSequentialGroup()
                        .addComponent(editQueryButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearQueryButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, queryPanelLayout.createSequentialGroup()
                        .addComponent(criteriaLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(criteriaField))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, queryPanelLayout.createSequentialGroup()
                        .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(projectionLabel)
                            .addComponent(sortLabel))
                        .addGap(6, 6, 6)
                        .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(projectionField)
                            .addComponent(sortField)))))
        );

        queryPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {criteriaLabel, projectionLabel, sortLabel});

        queryPanelLayout.setVerticalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(criteriaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(criteriaLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(projectionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sortLabel))
                .addGap(12, 12, 12)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editQueryButton)
                    .addComponent(clearQueryButton)))
        );

        documentsToolBar.setFloatable(false);
        documentsToolBar.setRollover(true);

        treeTableViewButton.setAction(getTreeTableViewAction());
        resultsViewButtonGroup.add(treeTableViewButton);
        treeTableViewButton.setFocusable(false);
        treeTableViewButton.setHideActionText(true);
        treeTableViewButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        treeTableViewButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(treeTableViewButton);

        flatTableViewButton.setAction(getFlatTableViewAction());
        resultsViewButtonGroup.add(flatTableViewButton);
        flatTableViewButton.setFocusable(false);
        flatTableViewButton.setHideActionText(true);
        flatTableViewButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        flatTableViewButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(flatTableViewButton);
        documentsToolBar.add(jSeparator4);

        expandTreeButton.setAction(getExpandTreeAction());
        expandTreeButton.setFocusable(false);
        expandTreeButton.setHideActionText(true);
        expandTreeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        expandTreeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(expandTreeButton);

        collapseTreeButton.setAction(getCollapseTreeAction());
        collapseTreeButton.setFocusable(false);
        collapseTreeButton.setHideActionText(true);
        collapseTreeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        collapseTreeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(collapseTreeButton);
        documentsToolBar.add(jSeparator5);

        addButton.setAction(getAddDocumentAction());
        addButton.setFocusable(false);
        addButton.setHideActionText(true);
        addButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(addButton);

        deleteButton.setAction(getDeleteSelectedDocumentAction());
        deleteButton.setEnabled(false);
        deleteButton.setFocusable(false);
        deleteButton.setHideActionText(true);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(deleteButton);

        editButton.setAction(getEditSelectedDocumentAction());
        editButton.setEnabled(false);
        editButton.setFocusable(false);
        editButton.setHideActionText(true);
        editButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(editButton);

        exportButton.setAction(getExportQueryResultAction());
        exportButton.setFocusable(false);
        exportButton.setHideActionText(true);
        exportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(exportButton);
        documentsToolBar.add(jSeparator1);

        refreshDocumentsButton.setAction(getRefreshDocumentsAction());
        refreshDocumentsButton.setFocusable(false);
        refreshDocumentsButton.setHideActionText(true);
        refreshDocumentsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refreshDocumentsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(refreshDocumentsButton);

        navFirstButton.setAction(getNavFirstAction());
        navFirstButton.setHideActionText(true);
        documentsToolBar.add(navFirstButton);

        navLeftButton.setAction(getNavLeftAction());
        navLeftButton.setHideActionText(true);
        documentsToolBar.add(navLeftButton);

        navRightButton.setAction(getNavRightAction());
        navRightButton.setFocusable(false);
        navRightButton.setHideActionText(true);
        navRightButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        navRightButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(navRightButton);

        navLastButton.setAction(getNavLastAction());
        navLastButton.setFocusable(false);
        navLastButton.setHideActionText(true);
        navLastButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        navLastButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(navLastButton);
        documentsToolBar.add(jSeparator2);

        org.openide.awt.Mnemonics.setLocalizedText(pageSizeLabel, org.openide.util.NbBundle.getMessage(CollectionView.class, "CollectionView.pageSizeLabel.text_1")); // NOI18N
        documentsToolBar.add(pageSizeLabel);

        pageSizeField.setMaximumSize(new java.awt.Dimension(40, 2147483647));
        pageSizeField.setMinimumSize(new java.awt.Dimension(40, 20));
        pageSizeField.setPreferredSize(new java.awt.Dimension(40, 20));
        pageSizeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pageSizeFieldActionPerformed(evt);
            }
        });
        documentsToolBar.add(pageSizeField);
        documentsToolBar.add(jSeparator3);
        documentsToolBar.add(totalDocumentsLabel);
        documentsToolBar.add(pageCountLabel);

        resultPanel.setLayout(new java.awt.CardLayout());

        treeTableScrollPane.setViewportView(resultTreeTable);

        resultPanel.add(treeTableScrollPane, "TREE_TABLE");

        flatTableScrollPane.setViewportView(resultFlatTable);

        resultPanel.add(flatTableScrollPane, "FLAT_TABLE");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(resultPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(queryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(documentsToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(queryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(documentsToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pageSizeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pageSizeFieldActionPerformed
        final int pageSize = Integer.parseInt(pageSizeField.getText());
        changePageSize(pageSize);
    }//GEN-LAST:event_pageSizeFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton clearQueryButton;
    private javax.swing.JButton collapseTreeButton;
    private javax.swing.JTextField criteriaField;
    private javax.swing.JLabel criteriaLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JToolBar documentsToolBar;
    private javax.swing.JButton editButton;
    private javax.swing.JButton editQueryButton;
    private javax.swing.JButton expandTreeButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JScrollPane flatTableScrollPane;
    private javax.swing.JToggleButton flatTableViewButton;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JButton navFirstButton;
    private javax.swing.JButton navLastButton;
    private javax.swing.JButton navLeftButton;
    private javax.swing.JButton navRightButton;
    private javax.swing.JLabel pageCountLabel;
    private javax.swing.JTextField pageSizeField;
    private javax.swing.JLabel pageSizeLabel;
    private javax.swing.JTextField projectionField;
    private javax.swing.JLabel projectionLabel;
    private javax.swing.JPanel queryPanel;
    private javax.swing.JButton refreshDocumentsButton;
    @Getter
    private javax.swing.JTable resultFlatTable;
    private javax.swing.JPanel resultPanel;
    @Getter
    private org.jdesktop.swingx.JXTreeTable resultTreeTable;
    private javax.swing.ButtonGroup resultsViewButtonGroup;
    private javax.swing.JTextField sortField;
    private javax.swing.JLabel sortLabel;
    private javax.swing.JLabel totalDocumentsLabel;
    private javax.swing.JScrollPane treeTableScrollPane;
    private javax.swing.JToggleButton treeTableViewButton;
    // End of variables declaration//GEN-END:variables

    public Preferences prefs() {
        return NbPreferences.forModule(CollectionView.class).node(CollectionView.class.getName());
    }

    void loadPreferences() {
        final Preferences prefs = prefs();
        final String version = prefs.get("version", "1.0");
        final int pageSize = prefs.getInt("result-view-table-page-size", collectionQueryResult.getPageSize());
        collectionQueryResult.setPageSize(pageSize);
        pageSizeField.setText(String.valueOf(pageSize));
        final String resultViewPref = prefs.get("result-view", ResultView.TREE_TABLE.name());
        final ResultView rView = ResultView.valueOf(resultViewPref);
        resultViewButtons.get(rView).setSelected(true);
        changeResultView(rView);
        refreshResults();
    }

    void writePreferences() {
        final Preferences prefs = prefs();
        prefs.put("version", "1.0");
        prefs.putInt("result-view-table-page-size", collectionQueryResult.getPageSize());
        prefs.put("result-view", resultView.name());
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Getter
    private final Action editQueryAction = new EditQueryAction(this);

    @Getter
    private final Action clearQueryAction = new ClearQueryAction(this);

    @Getter
    private final Action addDocumentAction = new AddDocumentAction(this);

    @Getter
    private final Action deleteSelectedDocumentAction = new DeleteSelectedDocumentAction(this);

    @Getter
    private final Action editSelectedDocumentAction = new EditSelectedDocumentAction(this);

    @Getter
    private final Action refreshDocumentsAction = new RefreshDocumentsAction(this);

    @Getter
    private final Action navFirstAction = new NavFirstAction(this);

    @Getter
    private final Action navLeftAction = new NavLeftAction(this);

    @Getter
    private final Action navRightAction = new NavRightAction(this);

    @Getter
    private final Action navLastAction = new NavLastAction(this);

    @Getter
    private final Action exportQueryResultAction = new ExportQueryResultAction(this);

    @Getter
    private final Action treeTableViewAction = ChangeResultViewAction.create(this, ResultView.TREE_TABLE);

    @Getter
    private final Action flatTableViewAction = ChangeResultViewAction.create(this, ResultView.FLAT_TABLE);

    @Getter
    private final Action collapseTreeAction = new CollapseAllDocumentsAction(this);

    @Getter
    private final Action expandTreeAction = new ExpandAllDocumentsAction(this);
    
    public enum ResultView {

        FLAT_TABLE, TREE_TABLE

    }

    private JPopupMenu createTreeTableContextMenu(TreePath treePath) {
        final JPopupMenu menu = new JPopupMenu();
        if (treePath != null) {
            final CollectionViewTreeTableNode node = (CollectionViewTreeTableNode) treePath.getLastPathComponent();
            final DocumentNode documentNode = (DocumentNode) treePath.getPathComponent(1);
            menu.add(new JMenuItem(new CopyDocumentToClipboardAction(documentNode.getUserObject())));
            if (node != documentNode) {
                if (node instanceof JsonPropertyNode) {
                    final JsonProperty property = ((JsonPropertyNode) node).getUserObject();
                    menu.add(new JMenuItem(new CopyKeyToClipboardAction(property)));
                    menu.add(new JMenuItem(new CopyValueToClipboardAction(property.getValue())));
                } else {
                    menu.add(new JMenuItem(new CopyValueToClipboardAction(node.getUserObject())));
                }
            }
            menu.addSeparator();
            menu.add(new JMenuItem(new EditSelectedDocumentAction(this)));
            menu.add(new JMenuItem(new DeleteSelectedDocumentAction(this)));
            menu.addSeparator();
        }
        menu.add(new JMenuItem(collapseTreeAction));
        menu.add(new JMenuItem(expandTreeAction));
        return menu;
    }

    private JPopupMenu createFlatTableContextMenu(int row, int column) {
        final JPopupMenu menu = new JPopupMenu();
        final DBObject document = collectionQueryResult.getDocuments().get(row);
        menu.add(new JMenuItem(new CopyDocumentToClipboardAction(document)));
        final DocumentsFlatTableModel model = (DocumentsFlatTableModel) resultFlatTable.getModel();
        final JsonProperty property = new JsonProperty(
            model.getColumnName(column),
            model.getValueAt(row, column));
        menu.add(new JMenuItem(new CopyKeyToClipboardAction(property)));
        menu.add(new JMenuItem(new CopyValueToClipboardAction(property.getValue())));
        menu.addSeparator();
        menu.add(new JMenuItem(new EditSelectedDocumentAction(this)));
        menu.add(new JMenuItem(new DeleteSelectedDocumentAction(this)));
        return menu;
    }

}
