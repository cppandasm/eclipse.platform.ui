package org.eclipse.ui.internal.keybindings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ui.IMemento;

public final class Path implements Comparable {

	public final static String TAG = "path";

	public static Path create() {
		return new Path(Collections.EMPTY_LIST);
	}

	public static Path create(Element element)
		throws IllegalArgumentException {
		return new Path(Collections.singletonList(element));
	}

	public static Path create(Element[] elements)
		throws IllegalArgumentException {
		return new Path(Arrays.asList(elements));
	}

	public static Path create(List elements)
		throws IllegalArgumentException {
		return new Path(elements);
	}

	public static Path read(IMemento memento)
		throws IllegalArgumentException {
		if (memento == null)
			throw new IllegalArgumentException();
			
		IMemento[] mementos = memento.getChildren(Element.TAG);
		
		if (mementos == null)
			throw new IllegalArgumentException();
		
		List elements = new ArrayList(mementos.length);
		
		for (int i = 0; i < mementos.length; i++)					
			elements.add(Element.read(mementos[i]));
		
		return Path.create(elements);
	}

	public static void write(IMemento memento, Path path)
		throws IllegalArgumentException {
		if (memento == null || path == null)
			throw new IllegalArgumentException();
			
		Iterator iterator = path.getElements().iterator();
		
		while (iterator.hasNext())
			Element.write(memento.createChild(Element.TAG), 
				(Element) iterator.next()); 
	}

	private List elements;

	private Path(List elements)
		throws IllegalArgumentException {
		super();
		
		if (elements == null)
			throw new IllegalArgumentException();
		
		this.elements = Collections.unmodifiableList(new ArrayList(elements));
		Iterator iterator = this.elements.iterator();
		
		while (iterator.hasNext())
			if (!(iterator.next() instanceof Element))
				throw new IllegalArgumentException();
	}

	public List getElements() {
		return elements;
	}

	public int match(Path path)
		throws IllegalArgumentException {
		if (path == null)
			throw new IllegalArgumentException();
			
		if (path.equalsOrIsChildOf(this)) { 
			int match = path.elements.size() - elements.size();
			
			if (match < 256)
				return match;
		}
		
		return -1;
	}

	public int compareTo(Object object) {
		if (!(object instanceof Path))
			throw new ClassCastException();

		return Util.compare(elements.iterator(), 
			((Path) object).elements.iterator());
	}
	
	public boolean equals(Object object) {
		return object instanceof Path && 
			elements.equals(((Path) object).elements);
	}

	public boolean equalsOrIsChildOf(Path path) {
		return elements.size() >= path.elements.size() && 
			elements.subList(0, path.elements.size()).equals(path.elements);
	}
			
	public boolean isChildOf(Path path) {
		return elements.size() > path.elements.size() && 
			elements.subList(0, path.elements.size()).equals(path.elements);
	}
}