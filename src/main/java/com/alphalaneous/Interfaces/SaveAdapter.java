package com.alphalaneous.Interfaces;

import com.alphalaneous.Components.EditCommandPanel;
import com.alphalaneous.Interactive.CustomData;

import java.util.HashMap;

public interface SaveAdapter {

    void save(HashMap<String, String> values, CustomData data, EditCommandPanel editCommandPanel);

}
