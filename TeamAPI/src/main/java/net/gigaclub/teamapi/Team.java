package net.gigaclub.teamapi;

import net.gigaclub.base.odoo.Odoo;
import org.apache.xmlrpc.XmlRpcException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Team {

    protected Odoo odoo;

    public Team(String hostname, String database, String username, String password) {
        this.odoo = new Odoo(hostname, database, username, password);
    }

    // Status Codes:
    // 4: Other error
    // 3: User has no access to create a team
    // 2: Team with name already exists
    // 1: Team could not be created
    // 0: Team created successfully
    public int createTeam(String playerUUID, String name) {
        try {
            return (int) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.team", "create_team", Arrays.asList(playerUUID, name)
            ));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return 4;
    }

    // Status Codes:
    // 4: Other error
    // 3: User has no access to create a team
    // 2: Team with name already exists
    // 1: Team could not be created
    // 0: Team created successfully
    public int createTeam(String playerUUID, String name, String description) {
        try {
            return (int) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.team", "create_team", Arrays.asList(playerUUID, name, description)
            ));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return 4;
    }

    // Status Codes:
    // 2: Other error
    // 1: No valid team found for this user
    // 0: Success
    public int editTeam(String playerUUID, int teamId, String newName) {
        try {
            return (int) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.team", "edit_team", Arrays.asList(playerUUID, teamId, newName)
            ));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return 2;
    }

    // Status Codes:
    // 2: Other error
    // 1: No valid team found for this user
    // 0: Success
    public int editTeam(String playerUUID, int teamId, String newName, String newDescription) {
        try {
            return (int) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.team", "edit_team", Arrays.asList(playerUUID, teamId, newName, newDescription)
            ));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return 2;
    }

    // Status Codes:
    // 3: Other error
    // 2: User has no permission to leave teams
    // 1: User has no team
    // 0: Success
    public int leaveTeam(String playerUUID, int teamId) {
        try {
            return (int) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.team", "leave_team", Arrays.asList(playerUUID, teamId)
            ));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return 3;
    }

    // Status Codes:
    // 5: Other error
    // 4: No valid team found for this user
    // 3: User to invite not found
    // 2: User is already member of team
    // 1: Request already sent
    // 0: Success
    public int inviteMember(String playerUUID, int teamId, String playerUUIDtoInvite) {
        try {
            return (int) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.team", "invite_member", Arrays.asList(playerUUID, teamId, playerUUIDtoInvite)
            ));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return 5;
    }

    // Status Codes:
    // 4: Other error
    // 3: User has no permission to accept requests
    // 2: Team does not exist
    // 1: Request does not exist
    // 0: Success
    public int acceptRequest(String playerUUID, int teamId) {
        try {
            return (int) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.team", "accept_request", Arrays.asList(playerUUID, teamId)
            ));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return 4;
    }

    // Status Codes:
    // 4: Other error
    // 3: User has no permission to reject requests
    // 2: Team does not exist
    // 1: Request does not exist
    // 0: Success
    public int denyRequest(String playerUUID, int teamId) {
        try {
            return (int) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.team", "deny_request", Arrays.asList(playerUUID, teamId)
            ));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return 4;
    }

    // Status Codes:
    // 5: Other error
    // 4: No valid team found for this user
    // 3: User to kick does not exist
    // 2: Not allowed to kick owner
    // 1: User is not member of team
    // 0: Success
    public int kickMember(String playerUUID, int teamId, String playerUUIDtoKick) {
        try {
            return (int) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.team", "kick_member", Arrays.asList(playerUUID, teamId, playerUUIDtoKick)
            ));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return 4;
    }

    public JSONArray getTeamsByMember(String playerUUID) {
        try {
            return new JSONArray(this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.team", "get_teams_by_member", Arrays.asList(playerUUID)
            )));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray getAllTeams() {
        try {
            return new JSONArray(this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.team", "get_all_teams", Arrays.asList()
            )));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getTeam(int teamId) {
        try {
            return new JSONObject((Map<String, String>) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.team", "get_team", Arrays.asList(teamId)
            )));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray getInvites(String playerUUID) {
        try {
            return new JSONArray(this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.request", "get_invites", Arrays.asList(playerUUID)
            )));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return null;
    }

}