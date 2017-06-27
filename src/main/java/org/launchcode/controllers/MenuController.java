package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CategoryDao;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * Created by Steven on 6/26/2017.
 */

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "My Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model){
        model.addAttribute(new Menu());
        model.addAttribute("title", "Add Menu");

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid Menu menu, Errors errors){

        if(errors.hasErrors()){
            model.addAttribute(errors);
            model.addAttribute(menu);
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);

        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable("menuId") int menuId, Menu menu){

        model.addAttribute("menu", menuDao.findOne(menuId));

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable("menuId") int menuId, Menu menu){

        Menu aMenu = menuDao.findOne(menuId);
        AddMenuItemForm menuItemForm = new AddMenuItemForm(aMenu, cheeseDao.findAll());
        model.addAttribute("form", menuItemForm);
        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "Add item to menu: " + aMenu.getName());

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.POST)
    public String addItem(Model model, @PathVariable("menuId") int menuId, @ModelAttribute @Valid AddMenuItemForm menuItemForm, Errors errors){

        if(errors.hasErrors()){
            Menu aMenu = menuDao.findOne(menuId);
            model.addAttribute("form", menuItemForm);
            model.addAttribute("cheeses", cheeseDao.findAll());
            model.addAttribute("title", "Add item to menu: " + aMenu.getName());
            model.addAttribute(errors);
            return "menu/add-item";
        }

        Menu theMenu = menuDao.findOne(menuItemForm.getMenuId());
        Cheese anItem = cheeseDao.findOne(menuItemForm.getCheeseId());
        theMenu.addItem(anItem);

        menuDao.save(theMenu);

        return "redirect:../view/" + theMenu.getId();
    }
}
