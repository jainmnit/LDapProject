package hello;

import java.io.IOException;
import java.util.List;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import model.Group;
import model.User;

@RestController
public class LDAPController {


	@Autowired
	LDapService ldapService;
	
	Logger logger = Logger.getLogger(this.getClass());
    
    @RequestMapping("/login")
    public boolean login() throws LdapException {
    	logger.debug("Comes in Login");
    	LdapConnection connection = ldapService.getConnection();
    	boolean isConnected = connection.isConnected();
    	ldapService.close(connection);
		return isConnected;
    }
    
    @RequestMapping("/searchUser")
    public List<User> searchUser(@RequestParam(value="filter", defaultValue="(cn=N*)") String searchExpression) throws LdapException, CursorException, IOException {
    	logger.debug("Comes in Search"+searchExpression);
    	return ldapService.searchUser(searchExpression);
    }
    
    @RequestMapping("/searchGroup")
    public List<Group> searchGroup(@RequestParam(value="filter", defaultValue="(cn=N*)") String searchExpression) throws LdapException, CursorException, IOException {
    	logger.debug("Comes in Search"+searchExpression);
    	return ldapService.searchGroup(searchExpression);
    }
    @RequestMapping(value = "/addUser",method = { RequestMethod.POST  },headers = {"Content-type=application/json"})
    public List<User> addUser(@RequestBody User user) throws LdapException, CursorException, IOException{
    	return ldapService.addUser(user);
    }
    
    @RequestMapping(value = "/modifyUser",method = { RequestMethod.PUT  },headers = {"Content-type=application/json"})
    public List<User> modifyUser(@RequestBody User user) throws LdapException, CursorException, IOException{
    	return ldapService.modifyUser(user);
    }
    
    @RequestMapping(value = "/addGroup",method = { RequestMethod.POST  },headers = {"Content-type=application/json"})
    public List<Group> addGroup(@RequestBody Group group) throws LdapException, CursorException, IOException{
    	return ldapService.addGroup(group);
    }
    
    @RequestMapping(value = "/addUserToGroup",method = { RequestMethod.POST  },headers = {"Content-type=application/json"})
    public List<Group> addUserToGroup(@RequestBody User user,@RequestParam(value="groupName") String groupName) throws LdapException, CursorException, IOException{
    	return ldapService.addUserToGroup(user,groupName);
    }
}