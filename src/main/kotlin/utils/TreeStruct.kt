package utils

import java.util.*

/**
 * Created by alewis on 10/02/2017.
 */

/**
 * Represents a Tree of Objects of generic type T. The Tree is represented as
 * a single rootElement which points to a List<Node></Node><T>> of children. There is
 * no restriction on the number of children that a particular node may have.
 * This Tree provides a method to serialize the Tree into a List by doing a
 * pre-order traversal. It has several methods to allow easy updation of Nodes
 * in the Tree.
</T> */
/**
 * Default ctor.
 */
open class Tree<T> {

    /**
     * Return the root Node of the tree.
     * @return the root element.
     */
    /**
     * Set the root Element for the tree.
     * @param rootElement the root element to set.
     */
    var rootElement: Node<T>? = null

    /**
     * Returns the Tree<T> as a List of Node<T> objects. The elements of the
     * List are generated from a pre-order traversal of the tree.
     * @return a List<Node></Node><T>>.
    </T></T></T> */
    fun toList(): List<Node<T>> {
        val list = ArrayList<Node<T>>()
        walk(rootElement!!, list)
        return list
    }

    /**
     * Returns a String representation of the Tree. The elements are generated
     * from a pre-order traversal of the Tree.
     * @return the String representation of the Tree.
     */
    override fun toString(): String {
        return toList().toString()
    }

    /**
     * Walks the Tree in pre-order style. This is a recursive method, and is
     * called from the toList() method with the root element as the first
     * argument. It appends to the second argument, which is passed by reference     * as it recurses down the tree.
     * @param element the starting element.
     * *
     * @param list the output of the walk.
     */
    private fun walk(element: Node<T>, list: MutableList<Node<T>>) {
        list.add(element)
        for (data in element.getChildren()) {
            walk(data, list)
        }
    }
}

/**
 * Represents a node of the Tree<T> class. The Node<T> is also a container, and
 * can be thought of as instrumentation to determine the location of the type T
 * in the Tree<T>.
</T></T></T> */
open class Node<T> {

    var data: T? = null
    var childrens: MutableList<Node<T>>? = null

    /**
     * Default ctor.
     */

    constructor()

    /**
     * Convenience ctor to create a Node<T> with an instance of T.
     * @param data an instance of T.
    </T> */
    constructor(data : T) : this() {
        this.data = data
    }

    /**
     * Return the children of Node<T>. The Tree<T> is represented by a single
     * root Node<T> whose children are represented by a List<Node></Node><T>>. Each of
     * these Node<T> elements in the List can have children. The getChildren()
     * method will return the children of a Node<T>.
     * @return the children of Node<T>
    </T></T></T></T></T></T></T> */
    fun getChildren(): List<Node<T>> {
        if (this.childrens == null) {
            return ArrayList()
        }
        return this.childrens as MutableList<Node<T>>
    }

    /**
     * Returns the number of immediate children of this Node<T>.
     * @return the number of immediate children.
    </T> */
    val numberOfChildren: Int
        get() {
            if (childrens == null) {
                return 0
            }
            return childrens!!.size
        }

    /**
     * Adds a child to the list of children for this Node<T>. The addition of
     * the first child will create a new List<Node></Node><T>>.
     * @param child a Node<T> object to set.
    </T></T></T> */
    fun addChild(child: Node<T>) {
        if (childrens == null) {
            childrens = ArrayList<Node<T>>()
        }
        childrens!!.add(child)
    }

    /**
     * Inserts a Node<T> at the specified position in the child list. Will     * throw an ArrayIndexOutOfBoundsException if the index does not exist.
     * @param index the position to insert at.
     * *
     * @param child the Node<T> object to insert.
     * *
     * @throws IndexOutOfBoundsException if thrown.
    </T></T> */
    @Throws(IndexOutOfBoundsException::class)
    fun insertChildAt(index: Int, child: Node<T>) {
        if (index == numberOfChildren) {
            // this is really an append
            addChild(child)
            return
        } else {
            childrens!![index] //just to throw the exception, and stop here
            childrens!!.add(index, child)
        }
    }

    /**
     * Remove the Node<T> element at index index of the List<Node></Node><T>>.
     * @param index the index of the element to delete.
     * *
     * @throws IndexOutOfBoundsException if thrown.
    </T></T> */
    @Throws(IndexOutOfBoundsException::class)
    fun removeChildAt(index: Int) {
        childrens!!.removeAt(index)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("{").append(data.toString()).append(",[")
        var i = 0
        for (e in getChildren()) {
            if (i > 0) {
                sb.append(",")
            }
            sb.append(e.data.toString())
            i++
        }
        sb.append("]").append("}")
        return sb.toString()
    }
}