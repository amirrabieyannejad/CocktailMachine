package com.example.cocktailmachine.data.db.elements;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.NewDatabaseConnection;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SQLRecipe extends DataBaseElement implements Recipe {
    private String name;
    //private List<Long> ingredientIds;
    //private HashMap<Long, Integer> ingredientPumpTime;
    private List<SQLRecipeIngredient> pumpTimes;
    private boolean alcoholic;
    private boolean available;
    private List<String> imageUrls;
    private List<Long> topics;

    public SQLRecipe(String name) {
        super();
        this.name = name;
        this.save();
    }

    public SQLRecipe(long ID,
                     String name,
                     boolean alcoholic,
                     boolean available){
        super(ID);
        this.name = name;
        this.alcoholic = alcoholic;
        this.available = available;
        this.loadUrls();
        this.loadPumpTime();
        this.loadTopics();
    }

    public SQLRecipe(long ID,
                     String name,
                     HashMap<Long, Integer> ingredientPumpTimes,
                     boolean alcoholic,
                     boolean available,
                     List<String> imageUrls,
                     List<Long> topics) {
        super(ID);
        this.name = name;
        this.alcoholic = alcoholic;
        this.available = available;
        this.imageUrls = imageUrls;
        this.topics = topics;
        this.addOrUpdateIDs(ingredientPumpTimes);
    }

    public SQLRecipe(long ID,
                     String name,
                     boolean alcoholic,
                     boolean available,
                     List<String> imageUrls,
                     List<Long> topics,
                     HashMap<Ingredient, Integer> ingredientPumpTimes) {
        super(ID);
        this.name = name;
        this.alcoholic = alcoholic;
        this.available = available;
        this.imageUrls = imageUrls;
        this.topics = topics;
        this.addOrUpdateElements(ingredientPumpTimes);
    }

    public void loadUrls(){
        this.imageUrls = NewDatabaseConnection.getDataBase().getUrls(this);
    }

    public void loadTopics(){
        this.topics = NewDatabaseConnection.getDataBase().getTopicIDs(this);
    }

    public void loadPumpTime(){
        this.pumpTimes = NewDatabaseConnection.getDataBase().getPumpTimes(this);
    }

    @Override
    public List<Long> getIngredientIds() {
        return this.pumpTimes
                .stream()
                .map(SQLRecipeIngredient::getIngredientID)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ingredient> getIngredients() {
        return Ingredient.getIngredients(this.getIngredientIds());
    }

    @Override
    public HashMap<Long, Integer> getIngredientPumpTime() {
        return (HashMap<Long, Integer>)
                this.pumpTimes.stream()
                        .collect( Collectors
                                .toMap(SQLRecipeIngredient::getID,
                                        SQLRecipeIngredient::getPumpTime));

    }

    private List<SQLRecipeIngredient> getRecipeIngredient(long ingredientID) throws NoSuchIngredientSettedException, TooManyTimesSettedIngredientEcxception {
        List<SQLRecipeIngredient> res = this.pumpTimes.stream().filter(ri-> ri.getIngredientID()==ingredientID).collect(Collectors.toList());
        if(res.size()==0){
            throw new NoSuchIngredientSettedException(this, ingredientID);
        }else if(res.size()>1){
            throw new TooManyTimesSettedIngredientEcxception(this, ingredientID);
        }
        return res;
    }

    @Override
    public int getSpecificIngredientPumpTime(long ingredientId)
            throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException {
        return this.getRecipeIngredient(ingredientId).get(0).getPumpTime();
    }

    @Override
    public int getSpecificIngredientPumpTime(Ingredient ingredient)
            throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException {
        return this.getSpecificIngredientPumpTime(ingredient.getID());
    }

    @Override
    public boolean isAlcoholic() {
        return this.alcoholic;
    }

    @Override
    public boolean isAvailable() {
        return this.available;
    }

    @Override
    public List<String> getImageUrls() {
        return this.imageUrls;
    }

    @Override
    public List<Long> getTopics() {
        return this.getTopics();
    }

    public void add(Ingredient ingredient, int timeInMilliseconds) throws AlreadySetIngredientException {
        this.add(ingredient.getID(), timeInMilliseconds);
    }

    public void add(long ingredientId, int timeInMilliseconds) throws AlreadySetIngredientException {
        if(this.getIngredientIds().contains(ingredientId)){
            throw new AlreadySetIngredientException(this, ingredientId);
        }
        this.pumpTimes.add(new SQLRecipeIngredient(ingredientId, this.getID(), timeInMilliseconds));
    }

    @Override
    public void addOrUpdate(Ingredient ingredient, int timeInMilliseconds) {
        this.addOrUpdate(ingredient.getID(), timeInMilliseconds);
    }

    @Override
    public void addOrUpdate(long ingredientId, int timeInMilliseconds) {
        if(this.pumpTimes.stream()
                .filter(pt -> pt.getIngredientID() == ingredientId)
                .peek(pt -> pt.setPumpTime(timeInMilliseconds)).count() == 0){
            this.pumpTimes.add(new SQLRecipeIngredient(ingredientId, this.getID(), timeInMilliseconds));
        }
    }

    public void addOrUpdateIDs(HashMap<Long, Integer> pumpTimes){
        pumpTimes.forEach(this::addOrUpdate);
    }

    public void addOrUpdateElements(HashMap<Ingredient, Integer> pumpTimes){
        pumpTimes.forEach(this::addOrUpdate);
    }

    @Override
    public void remove(Ingredient ingredient) {
        this.removeIngredient(ingredient.getID());
    }

    @Override
    public void removeIngredient(long ingredientId) {
        this.pumpTimes.removeAll(this.pumpTimes.stream()
                .filter(ri->ri.getIngredientID()==ingredientId)
                .peek(SQLRecipeIngredient::delete)
                .collect(Collectors.toList()));
    }

    @Override
    public void remove(Topic topic) {
        this.topics.remove(topic.getID());
    }

    @Override
    public void removeTopic(long topicId) {
        this.topics.remove(topicId);

    }

    @Override
    public void delete() {
        NewDatabaseConnection.getDataBase().remove(this);
    }

    @Override
    public void save() {
        NewDatabaseConnection.getDataBase().addOrUpdate(this);
        this.wasSaved();
    }

    @Override
    public String getName() {
        return this.name;
    }


}
