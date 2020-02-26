import mysql.connector
from bs4 import BeautifulSoup
import requests
from time import sleep


# Fix missing and unknown categories for apps as well as undefined application names
def fix_category_data(db):
    play_store_link = 'https://play.google.com/store/apps/details?id={}'
    cursor = db.cursor(named_tuple=True, buffered=True)
    query = f'SELECT * FROM {db_categories}'
    cursor.execute(query)
    unknown_apps = list()

    # data looks like this below
    # Row(app_name='Ziffit', category='UNDEFINED', app_package='uk.co.brightec.ziffit')
    for row in cursor.fetchall():
        pkg = row.app_package
        if row.category in ['UNDEFINED', None]:
            print(pkg)
            app_page = requests.get(play_store_link.format(pkg))  # 404 on this means it doesn't exist on google app store
            print(app_page.status_code)
            sleep(0.5)
            if app_page.status_code in [404, 500]:
                unknown_apps.append(row)
                continue

            soup = BeautifulSoup(app_page.text, 'lxml')
            genre = str(soup.find('a', itemprop='genre').next_element)
            print(genre)

            update_query = (f'REPLACE INTO {db_categories}'
                            f'(app_name, category, app_package)'
                            f'VALUES (%s, %s, %s)')

            cursor.execute(update_query, (row.app_name, genre, row.app_package))
            db.commit()

    cursor.close()


if __name__ == '__main__':
    db_categories = 'app_categories'
    db_calls = 'calls'
    db_loc = 'locations'
    db_user = 'users'
    db_session = 'user_session_data'

    connector = mysql.connector.connect(user='admin', password='password',
                                        host='127.0.0.1', database='Dissertation',
                                        use_pure=True)

    fix_category_data(connector)

    connector.close()

