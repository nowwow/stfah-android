package com.mspw.staythefuckathome.uploadvideo

import android.database.Cursor

class CursorIterable(
    private val iterableCursor: Cursor
) : Iterable<Cursor> {

    override fun iterator(): Iterator<Cursor> {
        return CursorIterator.from(iterableCursor)
    }

    class CursorIterator(
        private val iteratorCursor: Cursor
    ) : Iterator<Cursor> {

        override fun hasNext(): Boolean {
            return iteratorCursor.isClosed.not() && iteratorCursor.moveToNext()
        }

        override fun next(): Cursor {
            return iteratorCursor
        }

        companion object {
            fun from(cursor: Cursor): CursorIterator {
                return CursorIterator(cursor)
            }
        }

    }

    companion object {
        fun from(cursor: Cursor): CursorIterable {
            return CursorIterable(cursor)
        }

        private val TAG = CursorIterable::class.java.simpleName
    }

}