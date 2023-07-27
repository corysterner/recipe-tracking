class BTreeNode {

    /**
     * Array of the keys stored in the node.
     */
    long[] keys;
    /**
     * Array of the values[studentID] = recordID stored in the node. This will only be filled when the node is a leaf node.
     */
    long[] values;
    /**
     * Minimum degree (defines the range for number of keys)
     **/
    int t;
    /**
     * Pointers to the children, if this node is not a leaf.  If
     * this node is a leaf, then null.
     */
    BTreeNode[] children;
    /**
     * number of key-value pairs in the B-tree
     */
    int n;
    /**
     * true when node is leaf. Otherwise false
     */
    boolean leaf;

    /**
     * used only when passing BTreeNode object up to parent
     */
    long parentKey;

    public long getKey(int index) {
        return keys[index];
    }

    public int getIndex(long studentID){
        for (int i = 0; i < n; i++){
            if (this.keys[i] == studentID){
                return i;
            }
        }
        return -1;
    }

    public void updateIndex(int oldIndex, int newIndex){
        this.keys[newIndex] = this.keys[oldIndex];
        if (this.isLeaf()) this.values[newIndex] = this.keys[oldIndex];
        else this.children[newIndex + 1] = this.children[oldIndex + 1];
    }

    public long getRecordValue(long studentID) {
        int index = this.getIndex(studentID);
        if (index == -1) return -1;
        return values[index];
    }

    public void insertNewRecord(long studentID, long recordID) {
        for (int i = n; i >= 0; i--) {
            /*If the student ID is less than the value at the current index
                update the index and move on to the next value
            */
            if (i == 0){
                this.keys[0] = studentID;
                this.values[0] = recordID;
            }

            else if (studentID < this.keys[i -1]) {
                this.updateIndex(i - 1, i);

            }

            //If the student ID is greater than the value at the current index insert
            else {
                this.keys[i] = studentID;
                this.values[i] = recordID;
                break;
            }
        }
        this.setN(this.n + 1);
    }

    /*Insert a new key into a non-leaf node. Wrapper for when we are
    only given a node
     */
    public void insertNewKey(BTreeNode child){
        //Get key from node and call insert
        long newKey = child.parentKey;
        if (newKey != -1) this.insertNewKey(newKey, child);

        //Reset parent key
        child.parentKey = -1;
    }

    /**
     * Tag that inserts a new key to the current BTreeNode
     * given a key and a child
     */
    private void insertNewKey(long newKey, BTreeNode child){

        int index = -1;
        for (int i = n; i >= 0; i--){

            //If we're at the first index insert
            if (i == 0){
                this.keys[0] = newKey;
                this.insertNewChild(1, child); //The new child node will always be the index + 1 of the key
            }

            /*If the student ID is less than the value at the current index
            update the index and move on to the next value
             */
            else if (newKey < this.keys[i - 1]){
                this.updateIndex(i - 1, i);
            }

            else {
                index = i;
                this.keys[index] = newKey;
                this.insertNewChild(index + 1, child); //The new child node will always be the index + 1 of the key
                break;
            }
        }

        //Increment the count
        this.setN(n + 1);
    }
    public void insertNewChild(int index, BTreeNode childNode){
        this.children[index] = childNode;
    }

    /**
     * This routine splits a leaf node
     * Returns the new leaf node from the split
     */
    public BTreeNode split(long studentId, long recordId) {
        BTreeNode newNode = new BTreeNode(this.t, this.leaf);

        //Determine if the new key needs to be inserted into the lower of the split nodes
        int offset = 0;
        if (studentId < this.getKey(this.t - 1)){
            offset = 1;
        }

        //Insert values into the new leaf
        for (int i = this.t - offset; i < this.t * 2; i++) {
            newNode.insertNewRecord(this.keys[i], this.values[i]);
        }

        //Insert new key into the appropriate node
        if (offset==1){
            this.setN(this.t - 1);
            this.insertNewRecord(studentId, recordId);
        }
        else {
            newNode.insertNewRecord(studentId, recordId);
        }

        //Set the pointer from each Node
        newNode.setNext(this.getNext());
        this.setNext(newNode);

        //Set the parent key
        newNode.parentKey = newNode.getKey(0);

        //Make sure counts are set correctly
        this.setN(this.t);
        newNode.setN(this.t + 1);

        return newNode;
    }

    /**
     * Splits a non-leaf node and
     * @param childNode new child node to add after split
     * @return new Btree node from split
     */
    public BTreeNode split(BTreeNode childNode){
        long newKey = childNode.parentKey;
        BTreeNode newNode = new BTreeNode(this.t, this.leaf);

        //Determine if an offset is needed if the new key will not be inserted into the lower of the split nodes
        int offset = 0;
        if (newKey > this.keys[this.t]) offset = 1;

        //Insert children and keys into the upper node
        for (int i = this.t + offset; i < this.t * 2; i++) {
            newNode.insertNewKey(this.keys[i], this.children[i + 1]);
        }
        //Insert the t+1 child
        newNode.insertNewChild(0, this.children[this.t + offset]);

        //Insert new key into correct place
        if (offset == 1){
            newNode.setN(this.t - 1);
            newNode.insertNewKey(childNode); //upper node
        }
        else{
            this.setN(this.t - 1);
            this.insertNewKey(childNode); //lower node
        }

        //Correct for placement of child pointers if new key is the middle key
        if (this.getKey(this.t) == newKey){
            this.children[this.t] = this.children[this.t + 1];
        }

        //Set the parent key
        newNode.parentKey = this.keys[this.t];

        //Make sure Ns set correctly
        this.setN(this.t);
        newNode.setN(this.t);

        //Return the new node
        return newNode;
    }

    /**
     * Deletes a record from a node and updates the index
     * @param key: the key to delete from the node
     * @return: true if key found, false otherwise
     */
    public boolean deleteKey(long key){
        int index = this.getIndex(key);
        if (index == -1) return false;

        //Update the indexes of everything above the key
        for (int i = index; i < this.n - 1; i++){
            updateIndex(i + 1, i);
        }

        //Decreminent n
        this.setN(this.n - 1);
        return true;
    }

    public long redistribute(BTreeNode sourceNode, long parentKey){
        int keysToRedistribute = (sourceNode.getN() - this.t + 1)/2;
        long currentKey;
        long currentRecord;
        BTreeNode currentChild;
        long newKey;

        //Handling for when the source of keys is to the right of the current node
        if (this.keys[this.n - 1] < sourceNode.keys[0]){
            //Determine the new key
            if (this.isLeaf()) newKey = sourceNode.keys[keysToRedistribute];
            else newKey = sourceNode.keys[keysToRedistribute - 1];

            currentKey = parentKey;
            for (int i = 0; i < keysToRedistribute; i++){

                if (isLeaf()) {
                    currentKey = sourceNode.keys[i];
                    currentRecord = sourceNode.values[i];
                    this.insertNewRecord(currentKey, currentRecord);
                }

                else {
                    if (i != 0) currentKey = sourceNode.keys[i - 1]; //Need to realign because parent key will offset other keys

                    currentChild = sourceNode.children[i];
                    this.insertNewKey(currentKey, currentChild);
                }

                sourceNode.deleteKey(currentKey);
            }
        }

        //Handling for when the source of new elements is to the left of the current node
        else{
            newKey = sourceNode.keys[sourceNode.getN() - keysToRedistribute - 1];
            currentKey = parentKey;

            for (int i = sourceNode.getN() - 1; i >= sourceNode.getN() - keysToRedistribute; i--){

                //Insert the record in the new place
                if (isLeaf()) {
                    currentKey = sourceNode.keys[i];
                    currentRecord = sourceNode.values[i];
                    this.insertNewRecord(currentKey, currentRecord);
                }

                //Insert the node in the new place
                else {
                    if (i != sourceNode.getN() - 1) currentKey = sourceNode.keys[i];
                    currentChild = sourceNode.children[i + 1];
                    this.insertNewKey(currentKey, currentChild);

                    //Insert new key inserts the key as the second child but we need it to be the first one
                    this.children[1] = this.children[0];
                    this.children[0] = currentChild;
                }

                //Delete the key from the source node
                sourceNode.deleteKey(currentKey);
            }
        }

        return newKey;
    }
    public long merge(BTreeNode rightNode){
        return 1; //TODO
    }

    public BTreeNode getChild(int index) {
        return children[index];
    }

    public void setChildren(BTreeNode[] children) {
        this.children = children;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public BTreeNode getNext() {
        return next;
    }

    public void setNext(BTreeNode next) {
        this.next = next;
    }

    /**
     * Determines if the current node has space to insert a new item
     * @return true if there is space to insert a new element, false otherwise
     */
    public boolean hasSpace(){
        return (this.n < this.t * 2);
    }

    /**
     * Determines if the current node has extra elements that can be redistributed
     * @return true if there are extra elements, false otherwise
     */
    public boolean hasExtraElements(){
        return (this.n > this.t);
    }

    /**
     * Determines if the current node has the minimum number of required elements
     * @return true if the node has enough elements, false otherwise
     */
    public boolean hasEnoughElements(){
        return (this.n >= this.t);
    }

    /**
     * point to next node when it is a leaf node. Otherwise null
     */
    BTreeNode next;

    // Constructor
    BTreeNode(int t, boolean leaf) {
        this.t = t;
        this.leaf = leaf;
        this.keys = new long[2 * t];
        this.children = new BTreeNode[2 * t + 1];
        this.n = 0;
        this.next = null;
        this.values = new long[2 * t];
        this.parentKey = -1;
    }


}
