package com.clara.simple_todo_list_with_fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
		AddToDoItemFragment.NewItemCreatedListener,
		ToDoItemDetailFragment.MarkItemAsDoneListener,
		ToDoListFragment.ListItemSelectedListener {

	private static final String BUNDLE_KEY_TODO_ITEMS = "TODO ITEMS ARRAY LIST";

	private static final String TAG_ADD_NEW_FRAG = "ADD NEW FRAGMENT";
	private static final String TAG_LIST_FRAG = "LIST FRAGMENT";
	private static final String TAG_DETAIL_FRAG = "DETAIL FRAGMENT";

	private ArrayList<ToDoItem> mTodoItems;

	private static final String TAG = "MAIN ACTIVITY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			//no saved instance state - first time Activity been created
			//Create new ArrayList, and add Add and List fragments.
			Log.d(TAG, "onCreate has no instance state. Setting up ArrayList, adding List Fragment and Add Fragment");

			mTodoItems = new ArrayList<>();

			// Add example data for testing. Remove/edit these lines for testing app
			mTodoItems.add(new ToDoItem("Water plants", false));
			mTodoItems.add(new ToDoItem("Feed cat", true));
			mTodoItems.add(new ToDoItem("Grocery shopping", false));

			// create and add initial fragments (Add and List) to screen

			AddToDoItemFragment addToDoItemFragment = AddToDoItemFragment.newInstance();
			ToDoListFragment toDoListFragment = ToDoListFragment.newInstance(mTodoItems);
			ToDoItemDetailFragment detailFragment = ToDoItemDetailFragment.newInstance(new ToDoItem("", false));

			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();

			//add including tages to help find the fragments on screen, if tey need ot be updated

			ft.add(R.id.add_todo_view_container, addToDoItemFragment, TAG_ADD_NEW_FRAG);
			ft.add(R.id.todo_list_view_container, toDoListFragment, TAG_LIST_FRAG);
			ft.add(R.id.todo_detail_view_container, detailFragment, TAG_DETAIL_FRAG);

			ft.commit();

		} else {
			//There is saved instance state, so the app has already run,
			//and the Activity should already have fragments.
			//Restore saved instance state, the ArrayList
			mTodoItems = savedInstanceState.getParcelableArrayList(BUNDLE_KEY_TODO_ITEMS);
			Log.d(TAG, "onCreate has saved instance state ArrayList =  " + mTodoItems);
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outBundle) {
		// Save the list of ToDoItems when app is rotated
		super.onSaveInstanceState(outBundle);
		outBundle.putParcelableArrayList(BUNDLE_KEY_TODO_ITEMS, mTodoItems);
	}


	@Override
	public void newItemCreated(ToDoItem newItem) {

		Log.d(TAG, "Notified that this new item was created: " + mTodoItems);

		//  add item to mTodoItems ArrayList
		//  Notify the ToDoListFragment that the list data has changed
		mTodoItems.add(newItem);
		//get reference to TODOLIST frag tell this frag
		FragmentManager fm = getSupportFragmentManager();
		ToDoListFragment listFragment = (ToDoListFragment) fm.findFragmentByTag(TAG_LIST_FRAG);
		listFragment.notifyItemsChanged();
		hideKeyboard();


	}

	@Override
	public void itemSelected(ToDoItem selected) {

		Log.d(TAG, "Notified that this item was selected: " + selected);

		//  Create new ToDoItemDetailFragment with the selected ToDoItem
		//  show on screen

		FragmentManager fm = getSupportFragmentManager();
		ToDoItemDetailFragment toDoItemDetailFragment = (ToDoItemDetailFragment) fm.findFragmentByTag(TAG_DETAIL_FRAG);
		toDoItemDetailFragment.setTodoItem(selected);

//		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//
//		ToDoItemDetailFragment toDoItemDetailFragment = ToDoItemDetailFragment.newInstance(selected);
//		ft.replace(android.R.id.content, toDoItemDetailFragment, TAG_DETAIL_FRAG);
//
//		ft.addToBackStack(TAG_DETAIL_FRAG);
//		ft.commit();

	}

	@Override
	public void todoItemDone(ToDoItem doneItem) {

		Log.d(TAG, "Notified that this item is done: " + mTodoItems);

		// Remove item from list
		mTodoItems.remove(doneItem);
		//  Find ToDoListFragment and tell it that the  data has changed
		FragmentManager fm = getSupportFragmentManager();
		ToDoListFragment listFragment = (ToDoListFragment) fm.findFragmentByTag(TAG_LIST_FRAG);
		listFragment.notifyItemsChanged();
		//Find the Detail fragment and remove it, if it is on screen
//		FragmentTransaction ft = fm.beginTransaction();
//		ToDoItemDetailFragment detailFragment = (ToDoItemDetailFragment) fm.findFragmentByTag(TAG_DETAIL_FRAG);
//		if (detailFragment != null) {
//			ft.remove(detailFragment);
//		}
//
//		ft.commit();

	}

	private void hideKeyboard() {
		View mainView = findViewById(android.R.id.content);
		InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(mainView.getWindowToken(), 0);
	}

}
