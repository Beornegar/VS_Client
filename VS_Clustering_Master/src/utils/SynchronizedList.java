package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SynchronizedList <E> implements java.util.List <E>{

	private List<E> internalList = new ArrayList<E>();
	private Object lock = new Object();
	
	@Override
	public boolean add(E arg0) {	
		
		synchronized(lock)
		{
			return internalList.add(arg0);
		}
	}

	@Override
	public void add(int arg0, E arg1) {
		synchronized(lock)
		{
			internalList.add(arg0,arg1);
		}
	}

	

	@Override
	public void clear() {
		synchronized(lock)
		{
			internalList.clear();
		}
	}	

	@Override
	public E get(int arg0) {
		synchronized(lock)
		{
			return internalList.get(arg0);
		}
	}

	@Override
	public boolean isEmpty() {
		synchronized(lock)
		{
			return internalList.isEmpty();
		}
	}

	@Override
	public Iterator<E> iterator() {
		synchronized(lock)
		{
			return internalList.iterator();
		}
	}

	@Override
	public ListIterator<E> listIterator() {
		synchronized(lock)
		{
			return internalList.listIterator();
		}
	}

	@Override
	public ListIterator<E> listIterator(int arg0) {
		synchronized(lock)
		{
			return internalList.listIterator();
		}
	}

	@Override
	public int size() {
		synchronized(lock)
		{
			return internalList.size();
		}
	}

	@Override
	public java.util.List<E> subList(int arg0, int arg1) {
		synchronized(lock)
		{
			return internalList.subList(arg0, arg1);
		}
	}

	@Override
	public boolean contains(Object arg0) {
		synchronized(lock)
		{
			return internalList.contains(arg0);
		}
	}

	@Override
	public int indexOf(Object arg0) {
		synchronized(lock)
		{
			return internalList.indexOf(arg0);
		}
	}

	@Override
	public int lastIndexOf(Object arg0) {
		synchronized(lock)
		{
			return internalList.lastIndexOf(arg0);
		}
	}

	@Override
	public boolean remove(Object arg0) {
		synchronized(lock)
		{
			return internalList.remove(arg0);
		}
	}

	@Override
	public E remove(int arg0) {
		synchronized(lock)
		{
			return internalList.remove(arg0);
		}
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		synchronized(lock)
		{
			return internalList.removeAll(arg0);
		}
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		synchronized(lock)
		{
			return internalList.retainAll(arg0);
		}
	}

	@Override
	public E set(int arg0, E arg1) {
		synchronized(lock)
		{
			return internalList.set(arg0, arg1);
		}
	}

	@Override
	public Object[] toArray() {
		synchronized(lock)
		{
			return internalList.toArray();
		}
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		synchronized(lock)
		{
			return internalList.toArray(arg0);
		}
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		synchronized(lock)
		{
			return internalList.addAll(arg0);
		}
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends E> arg1) {
		synchronized(lock)
		{
			return internalList.addAll(arg0, arg1);
		}
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		synchronized(lock)
		{
			return internalList.containsAll(arg0);
		}
	}

	

}
