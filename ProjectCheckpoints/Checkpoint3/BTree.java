import java.util.ArrayList;
import java.util.List;

/**
 * B+Tree Structure
 * Key - StudentId
 * Leaf Node should contain [ key,recordId ]
 */
class BTree {

    /**
     * Pointer to the root node.
     */
    private BTreeNode root;
    /**
     * Number of key-value pairs allowed in the tree/the minimum degree of B+Tree
     **/
    private int t;

    BTree(int t) {
        this.root = new BTreeNode(t, true);
        this.t = t;
    }

    long search(long studentId) {
        /*
         * Implement this function to search in the B+Tree.
         * Return recordID for the given StudentID.
         * Otherwise, print out a message that the given studentId has not been found in the table and return -1.
         */
        BTreeNode leafNode = findLeaf(root, studentId);
        return leafNode.getRecordValue(studentId);
    }

    /**
     * Given a student ID and a parent node find the leaf node
     * @param currentNode - node to find leaf of
     * @param studentID - student Id key to find leaf for
     * @return - leaf BTreeNode for the given key
     */
    private BTreeNode findLeaf(BTreeNode currentNode, long studentID){
        if (currentNode.leaf){
            return currentNode;
        }

        else {
            BTreeNode childNode = findChildNode(currentNode, studentID);
            return findLeaf(childNode, studentID);
        }
    }

    /**
     * Find the child node for a given key
     * @param parentNode - parent node to find child for
     * @param studentID - key used to find corresponding child
     * @return - child node for the given key
     */
    private BTreeNode findChildNode(BTreeNode parentNode, long studentID){
        int childIndex = findChildNodeIndex(parentNode, studentID);
        return parentNode.getChild(childIndex);
    }

    /**
     * Find the index of a child node given a student Id and parent node
     * @param parentNode - parent node
     * @param studentID - key
     * @return - index of child node
     */
    private int findChildNodeIndex(BTreeNode parentNode, long studentID){
        int n = parentNode.getN();

        //Look for the index at which the key is lower than studentID
        for (int i = 0; i < n; i++){
            if (studentID < parentNode.getKey(i)){
                return i;
            }
        }

        //If studentID was greater than all keys set the child index to the last one
        return n;
    }

    BTree insert(Student student) {
        /**
         * Implement this function to insert in the B+Tree.
         * Also, insert in student.csv after inserting in B+Tree.
         */
        BTreeNode newChildNode = treeInsert(this.root, student);

        //Handling for when the root node is split
        if (newChildNode.parentKey != -1) {

            //Create new root
            BTreeNode newRoot = new BTreeNode(this.t, false);

            //Insert the current root and split child as its children nodes
            newRoot.insertNewChild(0, this.root);
            newRoot.insertNewKey(newChildNode);

            //Assign the root to the new root
            this.root = newRoot;

        }
        System.out.println(student.studentId);

       //printTest();
        System.out.println(print().toString());
        return this;
    }

    /**
     * Recursive function to insert to the BTree
     * @param currentNode - current BTreeNode
     * @param student - student object to insert
     * @return - new BTreeNode if split happened
     */
    BTreeNode treeInsert(BTreeNode currentNode, Student student){
        //Handling for leaf nodes
        if (currentNode.isLeaf()){

            //If there is space, just add the entry then you're done!
            if(currentNode.hasSpace()){
                currentNode.insertNewRecord(student.studentId, student.recordId);
                currentNode.parentKey = -1;
                return currentNode;
            }

            //If there isn't space perform a split and return the new key value to the caller
            else{
                //Insert the new record before the split in order to correctly order
                return currentNode.split(student.studentId, student.recordId);
            }
        }

        //Handling of non-leaf nodes
        else{
            BTreeNode childNode = findChildNode(currentNode, student.studentId);
            BTreeNode newChildNode = treeInsert(childNode, student);

            //If the child was not split, nothing else needs to be done
            if (newChildNode.parentKey == -1) return newChildNode;

                //If the child was split handle appropriately
            else{
                if (currentNode.hasSpace()){
                    currentNode.insertNewKey(newChildNode);
                    return currentNode;
                }

                else{
                    return currentNode.split(newChildNode);
                }
            }
        }
    }

    void fileInsert(Student student){
        //TODO
    }

    boolean delete(long studentId) {
        /**
         * Implement this function to delete in the B+Tree.
         * Also, delete in student.csv after deleting in B+Tree, if it exists.
         * Return true if the student is deleted successfully otherwise, return false.
         */
        boolean success = treeDelete(this.root, studentId);

        //Reset the root node if its empty
        if (!this.root.isLeaf() && (this.root.getN() == 0)){
            this.root = findChildNode(this.root, studentId);
        }

        //printTest();
        return true;
    }

    /**
     * Delete from the BTree given a key
     * @param currentNode - current node to delete from
     * @param studentId - key to delete
     * @return: true if key found and deleted, false otherwise
     */
    boolean treeDelete(BTreeNode currentNode, long studentId){
        //If this is a leaf, attempt to delete key and return
        if (currentNode.isLeaf()){
            return currentNode.deleteKey(studentId);
        }

        else{
            //Find and delete from the childNode recursively
            BTreeNode childNode = findChildNode(currentNode, studentId);
            boolean wasDeleted = treeDelete(childNode, studentId);

            //If deletion occurred check if there are still enough elements
            if (wasDeleted && !childNode.hasEnoughElements()) {
                //Add elements to the child from siblings
                replenishChild(currentNode, studentId);
            }

            return wasDeleted;
        }
    }

    /**
     * Add elements to the child from sibilings
     * @param parentNode - parent node
     * @param studentId - student Id that was deleted
     */
    void replenishChild (BTreeNode parentNode, long studentId){
        int childNodeIndex = findChildNodeIndex(parentNode, studentId);
        BTreeNode childNode = parentNode.getChild(childNodeIndex);

        BTreeNode leftChild = null;
        BTreeNode rightChild = null;
        long newKey;

        if (childNodeIndex > 0) leftChild = parentNode.getChild(childNodeIndex - 1);
        if (childNodeIndex < parentNode.getN()) rightChild = parentNode.getChild(childNodeIndex + 1);

        //Try to find extra elements from one of the siblings
        if (rightChild != null && rightChild.hasExtraElements()){
            newKey = childNode.redistribute(rightChild, parentNode.getKey(childNodeIndex));
            parentNode.keys[childNodeIndex] = newKey;
        }

        else if (leftChild != null && leftChild.hasExtraElements()){
            newKey = childNode.redistribute(leftChild, parentNode.getKey(childNodeIndex - 1));
            parentNode.keys[childNodeIndex - 1] = newKey;
        }

        //If no siblings had extra elements then merge with one of them
        else if (rightChild != null){
            childNode.merge(rightChild, parentNode.keys[childNodeIndex]);
            parentNode.deleteKey(childNodeIndex);
        }

        else if (leftChild != null){
            leftChild.merge(childNode, parentNode.keys[childNodeIndex - 1]);
            parentNode.deleteKey(childNodeIndex - 1);
        }

        //This should never happen
        else {
            System.out.println("Something went wrong with deletion merging/redistribution");
        }
    }

    List<Long> print() {

        List<Long> listOfRecordID = new ArrayList<>();

        /*
         * Implement this function to print the B+Tree.
         * Return a list of recordIDs from left to right of leaf nodes
         */

        //Get the leaf node for 0 which will be the farthest to the left
        BTreeNode currentNode = findLeaf(this.root, 0);

        //Loop over all the leaf nodes and the records they hold
        while (currentNode != null) {
            for (int i = 0; i < currentNode.getN(); i++){
                listOfRecordID.add(currentNode.values[i]);
            }
            currentNode = currentNode.getNext();
        }
        return listOfRecordID;
    }

    void printTest(){
        printNodeKeys(this.root, 0);

        List<Long> listOfStudentId = new ArrayList<>();
        BTreeNode currentNode = findLeaf(this.root, 0);

        System.out.println();
    }

    void printNodeKeys(BTreeNode currentNode, int level){
        List<Long> listOfStudentId = new ArrayList<>();
        for (int i = 0; i < currentNode.getN(); i++){
            listOfStudentId.add(currentNode.getKey(i));
        }
        System.out.print(" List of keys for node in level " + level + ": " + listOfStudentId.toString());

        if (currentNode.getChild(0) != null){
            System.out.println();
            for (int i = 0; i <= currentNode.getN(); i++){
                printNodeKeys(currentNode.getChild(i), level + 1);
            }
            System.out.println();
        }
    }
}
