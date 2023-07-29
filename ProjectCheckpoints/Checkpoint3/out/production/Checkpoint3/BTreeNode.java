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

    /**
     * Gets the key given an index
     * @param index - index of key
     * @return - key
     */
    public long getKey(int index) {
        return keys[index];
    }

    /**
     * Gets the index of the key
     * @param studentID - key
     * @return index
     */
    public int getIndex(long studentID){
        for (int i = 0; i < n; i++){
            if (this.keys[i] == studentID){
                return i;
            }
        }
        return -1;
    }

    /**
     * Update the index of a leaf or node handling record pointers and children
     * @param oldIndex - current index of key
     * @param newIndex - new index for key
     */
    public void updateIndex(int oldIndex, int newIndex){
        this.keys[newIndex] = this.keys[oldIndex];
        if (this.isLeaf()) this.values[newIndex] = this.keys[oldIndex];
        else this.children[newIndex + 1] = this.children[oldIndex + 1];
    }

    /**
     * Get the value of a record given a student ID
     * @param studentID - key to find record with
     * @return - record ID
     */
    public long getRecordValue(long studentID) {
        int index = this.getIndex(studentID);
        if (index == -1) return -1;
        return values[index];
    }

    /**
     * Insert a record into a leaf node at the correct spot
     * @param studentID - the key for the record
     * @param recordID - the record Id
     */
    public void insertNewRecord(long studentID, long recordID) {
        for (int i = n; i >= 0; i--) {
            /*If the student ID is less than the value at the current index
                update the index and move on to the next value
            */
            if (i == 0){
                this.keys[0] = studentID;
                this.values[0] = recordID;
                break;
            }

            //If the input key is less than the lower key update the index and move on
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

        //Update the count
        this.setN(this.n + 1);
    }

    /**
     * Insert a new key in the correct place given a child node with parentKey set
     * @param child - child node to insert a key for
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

            /*If the student ID is less than the value at the lower index
            update the index and move on to the next value
             */
            else if (newKey < this.keys[i - 1]){
                this.updateIndex(i - 1, i);
            }

            //If the student Id is greater than the value at the lower index then insert it here
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

    /**
     * Insert a child given an index and a child
     * @param index - index to insert child
     * @param childNode - child node to insert
     */
    public void insertNewChild(int index, BTreeNode childNode){
        this.children[index] = childNode;
    }

    /**
     * Splits a leaf node
     * @param studentId - the new student Id to insert after the split
     * @param recordId - the new record Id to insert after the split
     * @return - the new BTreeNode created by the split
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
     * Splits a non-leaf
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

        this.deleteKey(index);

        return true;
    }

    /**
     * Delete a key from the given index and update the rest of the indexes
     * @param index - index to delete a key from
     */
    public void deleteKey(int index){
        //Update the indexes of everything above the key
        for (int i = index; i < this.n - 1; i++){
            updateIndex(i + 1, i);
        }

        //Decreminent n
        this.setN(this.n - 1);
    }

    /**
     * Redistribute keys between a node that has too many and one that has too little
     * @param sourceNode - source of keys (node with extra keys)
     * @param parentKey - key of the parent
     * @return - the new parent key for the index
     */
    public long redistribute(BTreeNode sourceNode, long parentKey){
        int keysToRedistribute = (sourceNode.getN() - this.t + 1)/2;
        long currentKey;
        long currentRecord;
        BTreeNode currentChild;
        long newParentKey;

        //Handling for when the source of keys is to the right of the current node
        if (this.keys[this.n - 1] < sourceNode.keys[0]){
            //Determine the new key
            if (this.isLeaf()) newParentKey = sourceNode.keys[keysToRedistribute];
            else newParentKey = sourceNode.keys[keysToRedistribute - 1];

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
            int sourceNodeStartingCount = sourceNode.getN();;
            newParentKey = sourceNode.keys[sourceNodeStartingCount - keysToRedistribute];
            currentKey = parentKey;

            for (int i = sourceNodeStartingCount - 1; i >= sourceNodeStartingCount - keysToRedistribute; i--){

                //Insert the record in the new place
                if (this.isLeaf()) {
                    currentKey = sourceNode.keys[i];
                    currentRecord = sourceNode.values[i];
                    this.insertNewRecord(currentKey, currentRecord);
                }

                //Insert the node in the new place
                else {
                    if (i != sourceNodeStartingCount- 1) currentKey = sourceNode.keys[i];
                    currentChild = sourceNode.children[i + 1];
                    this.insertNewKey(currentKey, currentChild);

                    //Insert new key inserts the key as the second child but we need it to be the first one
                    this.children[1] = this.children[0];
                    this.children[0] = currentChild;
                }

                //Delete the key from the source node
                sourceNode.deleteKey(i);
            }
        }

        return newParentKey;
    }

    /**
     * Merge two nodes
     * @param rightNode - the node on the right
     * @param parentKey - the current parent key
     */
    public void merge(BTreeNode rightNode, long parentKey){
        int rightNodeStartingCount = rightNode.getN();
        long currentKey;
        long currentRecord;
        BTreeNode currentChild;

        //Handling to pull in parent key for non-leaf nodes
        if (!this.isLeaf()) this.insertNewKey(parentKey, rightNode.children[0]);

        //Move keys from right node to the left node
        for (int i = 0; i < rightNodeStartingCount; i++){
            currentKey = rightNode.keys[i];

            //Insert key into the left node
            if (this.isLeaf()){
                currentRecord = rightNode.values[i];
                this.insertNewRecord(currentKey, currentRecord);
            }

            else {
                currentChild = rightNode.children[i + 1];
                this.insertNewKey(currentKey, currentChild);
            }
        }

        //Reset the next node
        if (this.isLeaf()){
            this.next = rightNode.getNext();
        }
    }

    /**
     * Given an index get the child node
     * @param index - index of child
     * @return - child node
     */
    public BTreeNode getChild(int index) {
        return children[index];
    }

    /**
     * Get the value of n from the node
     * @return - n
     */
    public int getN() {
        return n;
    }

    /**
     * Set N for the node
     * @param n - the new value of n
     */
    public void setN(int n) {
        this.n = n;
    }

    /**
     * Returns whether the node is a leaf node
     * @return - true if a leaf node and false otherwise
     */
    public boolean isLeaf() {
        return leaf;
    }

    /**
     * Gets the next leaf node
     * @return - leaf node to the right
     */
    public BTreeNode getNext() {
        return next;
    }

    /**
     * Set the next leaf node
     * @param next - the next leaf node to be set
     */
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
