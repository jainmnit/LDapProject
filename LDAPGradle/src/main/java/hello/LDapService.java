package hello;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.Response;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchResultEntry;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import model.Group;
import model.User;

@Service
public class LDapService {
	
	Logger logger = Logger.getLogger(this.getClass());
	public LdapConnection getConnection() throws LdapException{
		LdapConnection connection = new LdapNetworkConnection( "localhost", 10389);
		connection.bind( "cn=Neeraj Jain,ou=users,o=test1", "neeraj123" );
		boolean isConnected = connection.isConnected();
		return connection;
	}
	
	public Boolean close(LdapConnection connection){
		try {
			connection.unBind();
			connection.close();
		} catch (LdapException e) {
			
		}catch (IOException e) {
		}
		
		return true;
	}
	
	public List<User> searchUser(String searchExpression) throws LdapException, CursorException, IOException {
		LdapConnection connection = getConnection();
		List<User> entries= new ArrayList<User>();
		SearchRequest req = new SearchRequestImpl();
		req.setScope( SearchScope.SUBTREE );
		req.addAttributes( "*" );
		req.setTimeLimit( 0 );
		req.setBase( new Dn( "ou=users,o=test1" ) );
		req.setFilter(searchExpression);

		// Process the request
		
		SearchCursor searchCursor = connection.search( req );
		User user = null;
		while ( searchCursor.next() )
		{
		    Response response = searchCursor.get();

		    // process the SearchResultEntry
		    if ( response instanceof SearchResultEntry )
		    {
		        Entry resultEntry = ( ( SearchResultEntry ) response ).getEntry();
		        user = new User();
		        String commonName = resultEntry.get("cn")==null?null:resultEntry.get("cn").getString();
		        String name[] = commonName.split(" ");
		        user.setFirstName(name[0]);
		        user.setGivenName(resultEntry.get("givenName")==null?null:resultEntry.get("givenName").getString());
		        String lastName = resultEntry.get("sn")==null?null:resultEntry.get("sn").getString();
		        if(StringUtils.isEmpty(lastName)){
		        	lastName = name[1];
		        }
		        user.setLastName(lastName);
		        user.setUserId(resultEntry.get("uid")==null?null:resultEntry.get("uid").getString());
		        //user.setUserPassword(resultEntry.get("userPassword")==null?null:resultEntry.get("userPassword").getBytes().toString());
		        user.setDn(resultEntry.getDn().getName());
		        entries.add(user);
		        System.out.println(resultEntry);
		    }
		}
		searchCursor.close();
	
	close(connection);
	return entries;

	}
	
	public List<Group> searchGroup(String searchExpression) throws LdapException, CursorException, IOException {
		LdapConnection connection = getConnection();
		List<Group> entries= new ArrayList<Group>();
		SearchRequest req = new SearchRequestImpl();
		req.setScope( SearchScope.SUBTREE );
		req.addAttributes( "*" );
		req.setTimeLimit( 0 );
		req.setBase( new Dn( "ou=groups,o=test1" ) );
		req.setFilter(searchExpression);

		// Process the request
		
		SearchCursor searchCursor = connection.search( req );
		Group group = null;
		while ( searchCursor.next() )
		{
		    Response response = searchCursor.get();

		    // process the SearchResultEntry
		    if ( response instanceof SearchResultEntry )
		    {
		        Entry resultEntry = ( ( SearchResultEntry ) response ).getEntry();
		        group = new Group();
		        String commonName = resultEntry.get("cn")==null?null:resultEntry.get("cn").getString();
		        group.setGroupName(commonName);
		        if(resultEntry.get("uniqueMember") !=null){
		        	Iterator<Value<?>> iterator = resultEntry.get("uniqueMember").iterator();
		        	while (iterator.hasNext()) {
						Value<?> value = (Value<?>) iterator.next();
						String searchUserExpression = value.getString();
						if(StringUtils.isNotEmpty(searchUserExpression)){
							group.getUsers().add(searchUser("("+searchUserExpression.split(",")[0].trim()+")").get(0));
						}
						
						
					}
		        }
		        entries.add(group);
		        System.out.println(resultEntry);
		    }
		}
	
		searchCursor.close();
	close(connection);
	return entries;

	}


	public List<User> addUser(User user) throws LdapException, CursorException, IOException {
		LdapConnection connection = getConnection();
		String commonName = user.getCommonName();
		String searchExression = "(cn="+commonName+")";
		List<User> search = searchUser(searchExression);
		if(search.isEmpty()){
			String dnString = "cn="+user.getCommonName()+",ou=users,o=test1";
			Dn systemDn1 = new Dn(dnString);
			DefaultEntry defaultEntry = new DefaultEntry(
					systemDn1, // The Dn
					"ObjectClass: top",
		            "ObjectClass: person",
		            "ObjectClass: inetOrgPerson",
		            "ObjectClass: organizationalPerson",
		            "sn", user.getLastName(),
		            "uid", user.getUserId(),
		            "userPassword", user.getUserPassword(),
		            "givenName", user.getGivenName());
			connection.add(defaultEntry);
		}
		search = searchUser(searchExression);
		return search;
	}
	
	public List<User> modifyUser(User user) throws LdapException, CursorException, IOException {
		LdapConnection connection = getConnection();
		String commonName = user.getCommonName();
		String searchExression = "(cn="+commonName+")";
		List<User> search = searchUser(searchExression);
		if(!search.isEmpty()){
			String dnString = "cn="+user.getCommonName()+",ou=users,o=test1";
			Dn systemDn1 = new Dn(dnString);
			DefaultEntry defaultEntry = new DefaultEntry(
					systemDn1, // The Dn
		            "sn", user.getLastName(),
		            "uid", user.getUserId(),
		            "userPassword", user.getUserPassword(),
		            "givenName", user.getGivenName());
			connection.modify(defaultEntry, ModificationOperation.REPLACE_ATTRIBUTE);
		}
		search = searchUser(searchExression);
		return search;
	}
	
	public List<Group> addGroup(Group group) throws LdapException, CursorException, IOException {
		LdapConnection connection = getConnection();
		String groupName = group.getGroupName();
		String searchExression = "(cn="+groupName+")";
		List<Group> search = searchGroup(searchExression);
		if(search.isEmpty()){
			String dnString = "cn="+groupName+",ou=groups,o=test1";
			Dn systemDn1 = new Dn(dnString);
			DefaultEntry defaultEntry = new DefaultEntry(
					systemDn1, // The Dn
					"ObjectClass: top",
		            "ObjectClass: groupOfUniqueNames",
		            "uniqueMember: cn=Neeraj Jain,ou=users,o=test1");
			connection.add(defaultEntry);
		}
		search = searchGroup(searchExression);
		return search;
	}

	public List<Group> addUserToGroup(User user, String groupName) throws LdapException, CursorException, IOException {
		LdapConnection connection = getConnection();
		String groupSearchExpression = "(cn="+groupName+")";
		List<Group> searchGroup = searchGroup(groupSearchExpression);
		if(!searchGroup.isEmpty()){
			Group group = searchGroup.get(0);
			String commonName = user.getCommonName();
			String userSearchExpression = "(cn="+commonName+")";
			List<User> searchUser = searchUser(userSearchExpression);
			User userToAdd;
			if(searchUser.isEmpty()){
				List<User> addUser = addUser(user);
				userToAdd = addUser.get(0);
			}else{
				userToAdd = searchUser.get(0);
			}
			String dnString = "cn="+groupName+",ou=groups,o=test1";
			Dn systemDn1 = new Dn(dnString);
			DefaultEntry defaultEntry = new DefaultEntry(
					systemDn1, // The Dn
		            "uniqueMember",userToAdd.getDn());
			connection.modify(defaultEntry, ModificationOperation.ADD_ATTRIBUTE);
		}
		searchGroup = searchGroup(groupSearchExpression);
		return searchGroup;
	}
	
}
