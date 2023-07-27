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

    private BTreeNode findLeaf(BTreeNode currentNode, long studentID){
        if (currentNode.leaf){
            return currentNode;
        }

        else {
            BTreeNode childNode = findChildNode(currentNode, studentID);
            return findLeaf(childNode, studentID);
        }
    }

    private BTreeNode findChildNode(BTreeNode parentNode, long studentID){
        int childIndex = findChildNodeIndex(parentNode, studentID);
        return parentNode.getChild(childIndex);
    }

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
        //fileInsert(student); TODO
        printTest();
        System.out.println(student.studentId);
        return this;
    }

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
         * TODO:
         * Implement this function to delete in the B+Tree.
         * Also, delete in student.csv after deleting in B+Tree, if it exists.
         * Return true if the student is deleted successfully otherwise, return false.
         */
        boolean success = treeDelete(this.root, studentId);
        printTest();
        //fileDelete(studentId);
        return true;
    }

    /**
     *
     * @param currentNode
     * @param studentId
     * @return: true if key found and deleted, false otherwise
     */
    boolean treeDelete(BTreeNode currentNode, long studentId){
        if (currentNode.isLeaf()){
            return currentNode.deleteKey(studentId);
        }

        else{
            //Find and deleted from the childNode recursively
            int childNodeIndex = findChildNodeIndex(currentNode, studentId);
            BTreeNode childNode = currentNode.getChild(childNodeIndex);
            boolean wasDeleted = treeDelete(childNode, studentId);

            //If deletion occured
            if (wasDeleted && !childNode.hasEnoughElements()){
                BTreeNode leftChild = null;
                BTreeNode rightChild = null;
                long newKey;

                if (childNodeIndex > 0) leftChild = currentNode.getChild(childNodeIndex - 1);
                if (childNodeIndex < currentNode.getN()) rightChild = currentNode.getChild(childNodeIndex + 1);

                if (rightChild != null && rightChild.hasExtraElements()){
                    newKey = childNode.redistribute(rightChild, currentNode.getKey(childNodeIndex));
                    currentNode.keys[childNodeIndex] = newKey;
                }

                else if (leftChild != null && leftChild.hasExtraElements()){
                    newKey = childNode.redistribute(leftChild, currentNode.getKey(childNodeIndex - 1));
                    currentNode.keys[childNodeIndex - 1] = newKey;
                }

                else if (rightChild != null){
                    //TODO
                }
                else if (leftChild != null){
                    //TODO
                }
                else {
                    System.out.println("Something went wrong with deletion merging/redistribution");
                }

            }

            return wasDeleted;
        }
    }

    void fileDelete(long studentId){
        //TODO
    }

    List<Long> print() {

        List<Long> listOfRecordID = new ArrayList<>();

        /*
         * Implement this function to print the B+Tree.
         * Return a list of recordIDs from left to right of leaf nodes.
         *
         */
        //Get the leaf node for 0 which will be the farthest to the left
        BTreeNode currentNode = findLeaf(this.root, 0);

        //Loop over all the leaf nodes and the records they hold
        while (currentNode != null) {
            for (int i = 0; i < currentNode.getN(); i++){
                listOfRecordID.add(currentNode.getKey(i));
            }
            currentNode = currentNode.getNext();
        }
        return listOfRecordID;
    }

    void printTest(){
        if (!this.root.isLeaf()) printNodeKeys(this.root);

        List<Long> listOfStudentId = new ArrayList<>();
        BTreeNode currentNode = findLeaf(this.root, 0);

        //Loop over all the leaf nodes and the records they hold
        System.out.println(" ");
        while (currentNode != null) {
            for (int i = 0; i < currentNode.getN(); i++){
                listOfStudentId.add(currentNode.getKey(i));
            }
            currentNode = currentNode.getNext();
        }
        System.out.println("List of recordIDs in B+Tree " + listOfStudentId.toString() + "\n");
    }

    void printNodeKeys(BTreeNode currentNode){
        List<Long> listOfStudentId = new ArrayList<>();
        for (int i = 0; i < currentNode.getN(); i++){
            listOfStudentId.add(currentNode.getKey(i));
        }
        System.out.print("List of keys in node " + listOfStudentId.toString());

        if (currentNode.getChild(0) != null){
            System.out.println("");
            for (int i = 0; i <= currentNode.getN(); i++){
                printNodeKeys(currentNode.getChild(i));
            }
            System.out.println("");
        }
    }
}
