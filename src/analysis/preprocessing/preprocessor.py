import mysql.connector


# Fix missing and unknown categories for apps as well as undefined application names
def fix_missing_categories(db):
    return


if __name__ == '__main__':
    db_categories = 'app_categories'
    db_calls = 'calls'
    db_loc = 'locations'
    db_user = 'users'
    db_session = 'user_session_data'

    connector = mysql.connector.connect(user='admin', password='password',
                                        host='127.0.0.1', database='Dissertation',
                                        use_pure=True)

    fix_missing_categories(connector)

    connector.close()

