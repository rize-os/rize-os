import React, { useEffect, useState} from 'react';
import { useParamsState } from "Frontend/hooks/use-params-state";
import { useSearchParams } from "react-router-dom";
import { Button, HorizontalLayout, Icon, Notification, TextField } from "@vaadin/react-components";
import UserListItem from "Frontend/components/user-list-item";
import { Apartment } from "@mui/icons-material";

import { OrganizationEndpoint, UserEndpoint } from "Frontend/generated/endpoints";
import type User from "Frontend/generated/rize/os/access/manager/user/User";
import type Organization from "Frontend/generated/rize/os/access/manager/organization/Organization";

export default function UserView() {

    const PAGE_SIZE = 50;

    const [searchParams] = useSearchParams();

    const [organizationFetchError, setOrganizationFetchError] = useState<string | undefined>(undefined);
    const [organizations, setOrganizations] = useState<Organization[]>([]);
    const [selectedOrganizationId, setSelectedOrganizationId] = useParamsState("organizationId", "");

    const [userFetchError, setUserFetchError] = useState<string | undefined>(undefined);
    const [searchTerm, setSearchTerm] = useState<string>("");
    const [searching, setSearching] = useState<boolean>(false);
    const [usersCount, setUsersCount] = useState<number>(999999);
    const [users, setUsers] = useState<User[]>([]);
    const [hasMore, setHasMore] = useState<boolean>(false);


    const fetchOrganizations = async () => {
        try {
            setOrganizations(await OrganizationEndpoint.findAll());
            setOrganizationFetchError(undefined);
        } catch (e) {
            resetOrganizations();
            setOrganizationFetchError("An error occurred while fetching organizations");
        }
    };

    const fetchInitialUsers = async () => {
        const usersInOrganization = await fetchUsersCount();
        await fetchUsers(0, usersInOrganization);
    }

    const fetchUsersCount = async () => {
        try {
            const count = await UserEndpoint.getUserCountForOrganization(selectedOrganizationId);
            setUsersCount(count);
            return count;
        } catch (e) {
            setUsersCount(999999);
            return 999999;
        }
    }

    const fetchMoreUsers = async () => {
        await fetchUsers(users.length);
    }

    const fetchUsers = async (offset : number, usersInOrganization? : number) => {
        setSearching(searchTerm.length > 0);

        try {
            const shownUsersCount = users.length;
            const fetchedUsers = await UserEndpoint.findUsersForOrganization(selectedOrganizationId, offset, PAGE_SIZE);
            setUserFetchError(undefined);

            if (offset === 0)
                setUsers(fetchedUsers);
            else
                setUsers(prevUsers => [...prevUsers, ...fetchedUsers]);

            usersInOrganization = usersInOrganization ?? usersCount;
            setHasMore(shownUsersCount + fetchedUsers.length < usersInOrganization);
        } catch (e) {
            resetUsers();
            setUserFetchError("An error occurred while fetching users");
        }

        setSearching(false);
    }

    const resetOrganizations = () => {
        setOrganizations([]);
        setSelectedOrganizationId("");
        resetUsers();
    }

    const resetUsers = () => {
        setUsers([]);
        setUsersCount(999999);
        setHasMore(false);
        setUserFetchError(undefined);
    }

    useEffect(() => {
        fetchOrganizations().then();
    }, []);

    useEffect(() => {
        if (searchParams.get("organizationId"))
            setSelectedOrganizationId(searchParams.get("organizationId")?.toString() ?? "");
        else if (!selectedOrganizationId || organizations.filter(o => o.id === selectedOrganizationId).length === 0)
            setSelectedOrganizationId(organizations[0]?.id ?? "");
    }, [organizations]);

    useEffect(() => {
        if (selectedOrganizationId)
            fetchInitialUsers().then();
    }, [selectedOrganizationId]);


    const createErrorNotification = (message: string, retryAction?: () => void) => {
        return (
            <Notification
                theme="error"
                position="bottom-end"
                opened={message !== ""}
                duration={0}
            >
                <HorizontalLayout theme="spacing" style={{ alignItems: 'center' }}>
                    <Icon icon="vaadin:warning" />
                    <div>{message}</div>
                    { retryAction &&
                        <Button style={{ margin: '0 0 0 var(--lumo-space-l)' }} onClick={retryAction}>
                            Retry
                        </Button>
                    }
                </HorizontalLayout>
            </Notification>
        );
    };

    return (
        <div className={"flex flex-col h-full"} id={"users-view"}>
            <header className={"flex flex-col gap-4 p-6 pb-4 box-border backdrop-blur sticky top-0 z-50  border-b"}>
                <div className={"flex gap-4"}>
                    <h2 className={"flex-grow text-2xl leading-10"}>Users</h2>
                    {/*}
                    <TextField key={"searchField"}
                               placeholder={"Search..."}
                               clearButtonVisible
                               value={searchTerm}
                               onValueChanged={e => setSearchTerm(e.detail.value)}>
                        {searching ? <Icon icon={"vaadin:spinner"} className={"animate-spin"} slot={"prefix"}/> :
                            <Icon icon={"vaadin:search"} slot={"prefix"}/>}
                    </TextField>
                    */}
                </div>
                <div className={"flex flex-wrap gap-2 box-border backdrop-blur sticky top-0 z-50"}>

                    {/* No Organization Placeholder */}
                    {organizations.length === 0 && (
                        <p className={"text-gray-500"}>No organizations found</p>
                    )}

                    {/* Organizations */}
                    {organizations.map((organization) => (
                        <div key={organization.id}
                             className={"border p-2 rounded-lg flex flex-row gap-2 cursor-pointer " + (selectedOrganizationId === organization.id ? "border-primary-900 bg-primary-100" : "")}
                             style={{borderColor: selectedOrganizationId === organization.id ? "--lumo-primary-color" : ""}}
                             onClick={() => setSelectedOrganizationId(organization.id)}>

                            <div className={"grid bg-gray-300 rounded-md w-6 h-6 content-center justify-center"}>
                                <Apartment className={"text-gray-500"} style={{transform: "scale(0.8)"}}/>
                            </div>
                            <p>{organization.name}</p>
                        </div>
                    ))}
                </div>
            </header>


            <div className={"flex flex-col p-6 pt-2 box-border"}>
                {/* No Organization Placeholder */}
                {organizations.length > 0 && users.length === 0 && (
                    <p className={"text-gray-500"}>No Users found</p>
                )}

                {/* User-List-Items */}
                {users.map((user, index) => (
                    <div key={user.id}>
                        <UserListItem  user={user}/>
                        {index < users.length - 1 && <div className={"h-px border border-t-0 m-1"}></div>}
                    </div>
                ))}

                {hasMore &&
                    <span className={"cursor-pointer text-primary-text text-sm underline mt-2"}
                          onClick={() => fetchMoreUsers()}>
                        Show more
                    </span>
                }
            </div>

            {organizationFetchError && createErrorNotification(organizationFetchError, fetchOrganizations)}
            {userFetchError && createErrorNotification(userFetchError, fetchInitialUsers)}
        </div>
    );
}